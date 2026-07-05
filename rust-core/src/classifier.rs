use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct LogFilter {
    pub levels: Option<Vec<String>>,
    pub tags: Option<Vec<String>>,
    pub keyword: Option<String>,
    pub pid: Option<i32>,
    pub favorites_only: Option<bool>,
    pub limit: Option<i64>,
    pub offset: Option<i64>,
}

impl LogFilter {
    pub fn from_json(json: &str) -> Self {
        serde_json::from_str(json).unwrap_or(LogFilter {
            levels: None,
            tags: None,
            keyword: None,
            pid: None,
            favorites_only: None,
            limit: None,
            offset: None,
        })
    }

    pub fn to_sql_where(&self) -> (String, Vec<Box<dyn rusqlite::types::ToSql>>) {
        let mut conditions = Vec::new();
        let mut params: Vec<Box<dyn rusqlite::types::ToSql>> = Vec::new();

        if let Some(ref levels) = self.levels {
            if !levels.is_empty() {
                let placeholders: Vec<String> = levels.iter().map(|_| "?".to_string()).collect();
                conditions.push(format!("level IN ({})", placeholders.join(",")));
                for l in levels {
                    params.push(Box::new(l.clone()));
                }
            }
        }

        if let Some(ref tags) = self.tags {
            if !tags.is_empty() {
                let placeholders: Vec<String> = tags.iter().map(|_| "?".to_string()).collect();
                conditions.push(format!("tag IN ({})", placeholders.join(",")));
                for t in tags {
                    params.push(Box::new(t.clone()));
                }
            }
        }

        if let Some(ref keyword) = self.keyword {
            if !keyword.is_empty() {
                conditions.push("(tag LIKE ? OR message LIKE ?)".to_string());
                let pattern = format!("%{}%", keyword);
                params.push(Box::new(pattern.clone()));
                params.push(Box::new(pattern));
            }
        }

        if let Some(pid) = self.pid {
            conditions.push("pid = ?".to_string());
            params.push(Box::new(pid));
        }

        if let Some(true) = self.favorites_only {
            conditions.push("is_favorite = 1".to_string());
        }

        let where_clause = if conditions.is_empty() {
            String::new()
        } else {
            format!("WHERE {}", conditions.join(" AND "))
        };

        (where_clause, params)
    }
}
