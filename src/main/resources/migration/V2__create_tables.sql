SET search_path TO patients_history;

-- Таблица пациентов
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY
);

COMMENT ON TABLE patients IS 'Таблица пациентов';
COMMENT ON COLUMN patients.id IS 'Идентификатор пациента';

-- Таблица диагнозов
CREATE TABLE IF NOT EXISTS diagnoses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    impact VARCHAR(500)
    );

COMMENT ON TABLE diagnoses IS 'Таблица диагнозов';
COMMENT ON COLUMN diagnoses.id IS 'Идентификатор диагноза';
COMMENT ON COLUMN diagnoses.name IS 'Название диагноза';
COMMENT ON COLUMN diagnoses.impact IS 'Влияние диагноза на самочувствие';

-- Таблица связей пациент ↔ диагноз (многие-ко-многим)
CREATE TABLE IF NOT EXISTS patient_diagnoses (
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    diagnosis_id BIGINT NOT NULL REFERENCES diagnoses(id) ON DELETE CASCADE,
    PRIMARY KEY (patient_id, diagnosis_id)
    );

COMMENT ON TABLE patient_diagnoses IS 'Связь пациентов и диагнозов (многие-ко-многим)';
COMMENT ON COLUMN patient_diagnoses.patient_id IS 'Ссылка на пациента';
COMMENT ON COLUMN patient_diagnoses.diagnosis_id IS 'Ссылка на диагноз';

-- Таблица партий медицинских данных
CREATE TABLE IF NOT EXISTS medical_data_batches (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL
    );

COMMENT ON TABLE medical_data_batches IS 'Таблица партий (пачек) медицинских данных пациента';
COMMENT ON COLUMN medical_data_batches.id IS 'Идентификатор партии медицинских данных';
COMMENT ON COLUMN medical_data_batches.patient_id IS 'Ссылка на пациента';
COMMENT ON COLUMN medical_data_batches.name IS 'Название партии медицинских данных (например, измерения за определенный период)';

-- Таблица медицинских данных
CREATE TABLE IF NOT EXISTS medical_data (
    id BIGSERIAL PRIMARY KEY,
    medical_data_batch_id BIGINT NOT NULL REFERENCES medical_data_batches(id) ON DELETE CASCADE,
    time_sec DOUBLE PRECISION NOT NULL,
    uterus DOUBLE PRECISION,
    bpm DOUBLE PRECISION
    );

COMMENT ON TABLE medical_data IS 'Таблица отдельных медицинских показаний (данные с приборов)';
COMMENT ON COLUMN medical_data.id IS 'Идентификатор записи медицинских данных';
COMMENT ON COLUMN medical_data.medical_data_batch_id IS 'Ссылка на партию медицинских данных';
COMMENT ON COLUMN medical_data.time_sec IS 'Время измерения в секундах';
COMMENT ON COLUMN medical_data.uterus IS 'Показатель активности матки (uterus)';
COMMENT ON COLUMN medical_data.bpm IS 'Частота сердечных сокращений плода (bpm)';
