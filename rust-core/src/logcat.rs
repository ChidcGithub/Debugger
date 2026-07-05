use std::io::BufRead;
use std::process::{Child, Command, Stdio};
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, Ordering};

use crate::parser;
use crate::storage;

static RUNNING: AtomicBool = AtomicBool::new(false);
static CHILD: Mutex<Option<Child>> = Mutex::new(Option::None);

pub fn start_capture() {
    if RUNNING.load(Ordering::SeqCst) {
        return;
    }

    let mut child = match Command::new("logcat")
        .args(["-v", "threadtime", "-b", "all"])
        .stdout(Stdio::piped())
        .stderr(Stdio::null())
        .spawn()
    {
        Ok(c) => c,
        Err(e) => {
            crate::java_callback("onError", &format!("Failed to start logcat: {}", e));
            return;
        }
    };

    let stdout = match child.stdout.take() {
        Some(s) => s,
        None => {
            crate::java_callback("onError", "Failed to capture logcat stdout");
            return;
        }
    };

    if let Ok(mut guard) = CHILD.lock() {
        *guard = Some(child);
    }
    RUNNING.store(true, Ordering::SeqCst);
    crate::java_callback("onCaptureStateChanged", "true");

    std::thread::spawn(move || {
        let reader = std::io::BufReader::new(stdout);

        for line in reader.lines() {
            if !RUNNING.load(Ordering::SeqCst) {
                break;
            }
            match line {
                Ok(text) => {
                    if let Some(entry) = parser::parse_line(&text) {
                        storage::insert_entry(&entry);
                        if let Ok(json) = serde_json::to_string(&entry) {
                            crate::java_callback("onLogEntry", &json);
                        }
                    }
                }
                Err(_) => break,
            }
        }

        if let Ok(mut guard) = CHILD.lock() {
            if let Some(mut c) = guard.take() {
                let _ = c.kill();
                let _ = c.wait();
            }
        }
    });
}

pub fn stop_capture() {
    RUNNING.store(false, Ordering::SeqCst);

    if let Ok(mut guard) = CHILD.lock() {
        if let Some(mut c) = guard.take() {
            let _ = c.kill();
            let _ = c.wait();
        }
    }

    crate::java_callback("onCaptureStateChanged", "false");
}
