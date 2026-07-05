use std::sync::Mutex;
use std::sync::OnceLock;

use rusqlite::Connection;

use crate::classifier::LogFilter;
use crate::parser::LogEntry;

static DB: OnceLock<Mutex<Connection>> = OnceLock::new();

fn get_db() -> &'static Mutex<Connection> {
    DB.get().expect("Database not initialized")
}

pub fn init(path: &str) {
    let conn = Connection::open(path).expect("Failed to open database");

    conn.execute_batch(
        "PRAGMA journal_mode=WAL;
         PRAGMA synchronous=OFF;

         CREATE TABLE IF NOT EXISTS logs (
             id INTEGER PRIMARY KEY AUTOINCREMENT,
             timestamp TEXT NOT NULL,
             pid INTEGER NOT NULL,
             tid INTEGER NOT NULL,
             level TEXT NOT NULL,
             tag TEXT NOT NULL,
             message TEXT NOT NULL,
             is_favorite INTEGER DEFAULT 0,
             captured_at TEXT NOT NULL DEFAULT (datetime('now'))
         );

         CREATE INDEX IF NOT EXISTS idx_logs_level ON logs(level);
         CREATE INDEX IF NOT EXISTS idx_logs_tag ON logs(tag);
         CREATE INDEX IF NOT EXISTS idx_logs_pid ON logs(pid);",
    )
    .expect("Failed to create tables");

    DB.set(Mutex::new(conn)).ok();
}

pub fn insert_entry(entry: &LogEntry) -> i64 {
    let db = get_db();
    let conn = db.lock().unwrap();
    conn.execute(
        "INSERT INTO logs (timestamp, pid, tid, level, tag, message) VALUES (?1, ?2, ?3, ?4, ?5, ?6)",
        rusqlite::params![
            entry.timestamp,
            entry.pid,
            entry.tid,
            entry.level,
            entry.tag,
            entry.message,
        ],
    )
    .expect("Failed to insert log entry");
    conn.last_insert_rowid()
}

pub fn query_entries(filter: &LogFilter) -> Result<Vec<LogEntry>, rusqlite::Error> {
    let (where_clause, params) = filter.to_sql_where();
    let limit = filter.limit.unwrap_or(500);
    let offset = filter.offset.unwrap_or(0);

    let sql = format!(
        "SELECT id, timestamp, pid, tid, level, tag, message, is_favorite FROM logs {} ORDER BY id DESC LIMIT ? OFFSET ?",
        where_clause
    );

    let db = get_db();
    let conn = db.lock().unwrap();
    let mut stmt = conn.prepare(&sql)?;

    let param_refs: Vec<&dyn rusqlite::types::ToSql> = params.iter()
        .map(|p| p.as_ref() as &dyn rusqlite::types::ToSql)
        .chain(std::iter::once(&limit as &dyn rusqlite::types::ToSql))
        .chain(std::iter::once(&offset as &dyn rusqlite::types::ToSql))
        .collect();

    let entries = stmt.query_map(param_refs.as_slice(), |row| {
        Ok(LogEntry {
            id: row.get(0)?,
            timestamp: row.get(1)?,
            pid: row.get(2)?,
            tid: row.get(3)?,
            level: row.get(4)?,
            tag: row.get(5)?,
            message: row.get(6)?,
            is_favorite: row.get::<_, i32>(7)? != 0,
        })
    })?;

    entries.collect()
}

pub fn get_logs(filter_json: &str) -> String {
    let filter = LogFilter::from_json(filter_json);
    match query_entries(&filter) {
        Ok(entries) => serde_json::to_string(&entries).unwrap_or_else(|_| "[]".to_string()),
        Err(_) => "[]".to_string(),
    }
}

pub fn clear_logs() {
    let db = get_db();
    let conn = db.lock().unwrap();
    conn.execute("DELETE FROM logs", []).ok();
    conn.execute("VACUUM", []).ok();
}

pub fn get_stats() -> String {
    let db = get_db();
    let conn = db.lock().unwrap();

    let total: i64 = conn
        .query_row("SELECT COUNT(*) FROM logs", [], |r| r.get(0))
        .unwrap_or(0);

    let levels = ["V", "D", "I", "W", "E", "F"];
    let mut counts = serde_json::Map::new();
    for level in &levels {
        let count: i64 = conn
            .query_row("SELECT COUNT(*) FROM logs WHERE level = ?", [level], |r| {
                r.get(0)
            })
            .unwrap_or(0);
        counts.insert(level.to_string(), serde_json::Value::from(count));
    }

    let mut top_tags = Vec::new();
    if let Ok(mut stmt) =
        conn.prepare("SELECT tag, COUNT(*) as cnt FROM logs GROUP BY tag ORDER BY cnt DESC LIMIT 10")
    {
        if let Ok(rows) = stmt.query_map([], |row| {
            let tag: String = row.get(0)?;
            let count: i64 = row.get(1)?;
            Ok((tag, count))
        }) {
            for row in rows.flatten() {
                top_tags.push(serde_json::json!({"tag": row.0, "count": row.1}));
            }
        }
    }

    let stats = serde_json::json!({
        "total": total,
        "levels": counts,
        "top_tags": top_tags,
    });

    stats.to_string()
}
