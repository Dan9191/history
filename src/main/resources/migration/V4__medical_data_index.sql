SET search_path TO patients_history;

CREATE INDEX IF NOT EXISTS idx_medical_data_batch_time
    ON medical_data (medical_data_batch_id, time_sec);
