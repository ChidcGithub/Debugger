use std::fs::File;
use std::io::Write;

use crate::classifier::LogFilter;
use crate::storage;

pub fn export(path: &str, format: &str, filter_json: &str) -> Result<(), String> {
    let filter = LogFilter::from_json(filter_json);
    let entries = storage::query_entries(&filter).map_err(|e| e.to_string())?;

    let mut file = File::create(path).map_err(|e| format!("Failed to create file: {}", e))?;

    match format {
        "json" => export_json(&mut file, &entries)?,
        "csv" => export_csv(&mut file, &entries)?,
        _ => export_txt(&mut file, &entries)?,
    }

    Ok(())
}

fn export_txt(file: &mut File, entries: &[crate::parser::LogEntry]) -> Result<(), String> {
    for entry in entries {
        writeln!(
            file,
            "[{}] [{}] (pid={}, tid={}) [{}]: {}",
            entry.timestamp, entry.level, entry.pid, entry.tid, entry.tag, entry.message
        )
        .map_err(|e| format!("Write error: {}", e))?;
    }
    Ok(())
}

fn export_json(file: &mut File, entries: &[crate::parser::LogEntry]) -> Result<(), String> {
    let json = serde_json::to_string_pretty(entries).map_err(|e| e.to_string())?;
    file.write_all(json.as_bytes())
        .map_err(|e| format!("Write error: {}", e))
}

fn export_csv(file: &mut File, entries: &[crate::parser::LogEntry]) -> Result<(), String> {
    let mut wtr = csv::Writer::from_writer(file);
    wtr.write_record(["timestamp", "pid", "tid", "level", "tag", "message"])
        .map_err(|e| e.to_string())?;

    for entry in entries {
        wtr.write_record([
            &entry.timestamp,
            &entry.pid.to_string(),
            &entry.tid.to_string(),
            &entry.level,
            &entry.tag,
            &entry.message,
        ])
        .map_err(|e| e.to_string())?;
    }

    wtr.flush().map_err(|e| e.to_string())
}
