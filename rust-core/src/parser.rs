use regex::Regex;
use serde::{Deserialize, Serialize};
use std::sync::OnceLock;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LogEntry {
    pub id: i64,
    pub timestamp: String,
    pub pid: i32,
    pub tid: i32,
    pub level: String,
    pub tag: String,
    pub message: String,
    pub is_favorite: bool,
}

fn logcat_re() -> &'static Regex {
    static RE: OnceLock<Regex> = OnceLock::new();
    RE.get_or_init(|| {
        Regex::new(
            r"^(\d{2}-\d{2})\s+(\d{2}:\d{2}:\d{2}\.\d{3})\s+(\d+)\s+(\d+)\s+([VDIWEF])\s+(.*?):\s(.*)$"
        ).expect("Invalid regex")
    })
}

pub fn parse_line(line: &str) -> Option<LogEntry> {
    let re = logcat_re();
    re.captures(line).map(|caps| {
        let date = caps.get(1).unwrap().as_str();
        let time = caps.get(2).unwrap().as_str();
        let pid: i32 = caps.get(3).unwrap().as_str().parse().unwrap_or(0);
        let tid: i32 = caps.get(4).unwrap().as_str().parse().unwrap_or(0);
        let level = caps.get(5).unwrap().as_str().to_string();
        let tag = caps.get(6).unwrap().as_str().to_string();
        let message = caps.get(7).unwrap().as_str().to_string();

        LogEntry {
            id: 0,
            timestamp: format!("{} {}", date, time),
            pid,
            tid,
            level,
            tag,
            message,
            is_favorite: false,
        }
    })
}


