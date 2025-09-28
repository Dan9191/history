SET search_path TO patients_history;

-- Таблица результатов родов
CREATE TABLE IF NOT EXISTS childbirth_results (
                                                  id INT PRIMARY KEY,
                                                  name VARCHAR(100) NOT NULL
    );
COMMENT ON TABLE childbirth_results IS 'Справочник результатов родов';
COMMENT ON COLUMN childbirth_results.id IS 'Идентификатор результата';
COMMENT ON COLUMN childbirth_results.name IS 'Название результата (Regular или Hypoxia)';


INSERT INTO childbirth_results (id, name) SELECT 1, 'Regular'
    WHERE NOT EXISTS (SELECT 1 FROM childbirth_results WHERE id = 1);
INSERT INTO childbirth_results (id, name) SELECT 2, 'Hypoxia'
    WHERE NOT EXISTS (SELECT 1 FROM childbirth_results WHERE id = 2);


-- Таблица пациентов
CREATE TABLE IF NOT EXISTS patients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INT,
    ph FLOAT,
    co2 FLOAT,
    glu FLOAT,
    lac FLOAT,
    be FLOAT,
    childbirth_result_id INT NOT NULL REFERENCES childbirth_results(id)
    );
COMMENT ON TABLE patients IS 'Таблица для хранения информации о пациентах';
COMMENT ON COLUMN patients.id IS 'Идентификатор пациента';
COMMENT ON COLUMN patients.name IS 'Имя пациента';
COMMENT ON COLUMN patients.age IS 'Возраст пациента';
COMMENT ON COLUMN patients.ph IS 'Кислотно-щелочной баланс';
COMMENT ON COLUMN patients.co2 IS 'Давление CO2 в артериальной крови';
COMMENT ON COLUMN patients.glu IS 'Уровень сахара в крови';
COMMENT ON COLUMN patients.lac IS 'Уровень молочной кислоты (лактат)';
COMMENT ON COLUMN patients.be IS 'Метаболический компонент регуляции pH';
COMMENT ON COLUMN patients.childbirth_result_id IS 'Ссылка на результат родов';

-- Таблица диагнозов
CREATE TABLE IF NOT EXISTS diagnoses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    impact VARCHAR(500)
    );

COMMENT ON TABLE diagnoses IS 'Таблица диагнозов';
COMMENT ON COLUMN diagnoses.id IS 'Идентификатор диагноза';
COMMENT ON COLUMN diagnoses.name IS 'Название диагноза';
COMMENT ON COLUMN diagnoses.description IS 'Описание диагноза';
COMMENT ON COLUMN diagnoses.impact IS 'Влияние диагноза на самочувствие';

-- Таблица связей пациент - диагноз (многие-ко-многим)
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
