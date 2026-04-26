CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gps_longitude DOUBLE PRECISION NOT NULL,
    gps_latitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    description TEXT,
    objective TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_project_dates CHECK (start_date < end_date)
);

CREATE TABLE IF NOT EXISTS project_files (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    filename VARCHAR(255) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    size BIGINT NOT NULL,
    binary_content BYTEA NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS drone_operations (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    project_id UUID NOT NULL,
    location_id UUID NOT NULL,
    drone VARCHAR(255) NOT NULL,
    objective TEXT,
    date DATE,
    description TEXT,
    flight_mode VARCHAR(100),
    weather_description TEXT,
    kp_index DOUBLE PRECISION,
    takeoff_time TIMESTAMP,
    landing_time TIMESTAMP,
    flight_length DOUBLE PRECISION DEFAULT 0,
    flight_duration_seconds INTEGER DEFAULT 0,
    avg_recording_altitude DOUBLE PRECISION DEFAULT 0,
    recording_length DOUBLE PRECISION DEFAULT 0,
    recording_start TIMESTAMP,
    recording_end TIMESTAMP,
    number_of_recordings INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT chk_drone_op_times CHECK (takeoff_time < landing_time),
    CONSTRAINT chk_drone_recording_times CHECK (recording_start < recording_end),
    CONSTRAINT chk_drone_flight_length_pos CHECK (flight_length >= 0),
    CONSTRAINT chk_drone_avg_altitude_pos CHECK (avg_recording_altitude >= 0),
    CONSTRAINT chk_drone_recording_length_pos CHECK (recording_length >= 0),
    CONSTRAINT chk_drone_number_recordings_pos CHECK (number_of_recordings >= 0)
);

CREATE TABLE IF NOT EXISTS drone_operation_files (
    id UUID PRIMARY KEY,
    drone_operation_id uuid NOT NULL,
    filename VARCHAR(255) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    size BIGINT NOT NULL,
    binary_content BYTEA NOT NULL,
    FOREIGN KEY (drone_operation_id) REFERENCES drone_operations(id) ON DELETE CASCADE
);
