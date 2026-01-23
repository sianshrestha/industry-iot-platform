-- 1. Enable TimescaleDB (The Time-Series superpower)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 2. Enable UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- Table 1: Devices (Inventory)
-- ==========================================
CREATE TABLE IF NOT EXISTS devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- e.g., 'CNC_MACHINE'
    firmware_version VARCHAR(50),
    status VARCHAR(20) NOT NULL CHECK (status IN ('REGISTERED', 'ACTIVE', 'DISABLED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

-- ==========================================
-- Table 2: Credentials (Security)
-- ==========================================
-- Why separate? Security best practice. If you accidentally dump the 'devices' table
-- in a log, you don't leak the passwords (hashes).
CREATE TABLE IF NOT EXISTS device_credentials (
    device_id UUID PRIMARY KEY REFERENCES devices(id) ON DELETE CASCADE,
    secret_hash VARCHAR(255) NOT NULL,
    last_rotated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

-- Device heartbeat/status tracking
CREATE TABLE IF NOT EXISTS device_status (
    device_id UUID PRIMARY KEY REFERENCES devices(id) ON DELETE CASCADE,
    online BOOLEAN DEFAULT FALSE,
    last_heartbeat_at TIMESTAMP WITH TIME ZONE
    );

-- ==========================================
-- Table 3: Sensor Readings (The Big Data)
-- ==========================================
CREATE TABLE IF NOT EXISTS sensor_readings (
    time TIMESTAMPTZ NOT NULL,   -- TimescaleDB requires a time column
    device_id UUID NOT NULL,
    sensor_type VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (device_id) REFERENCES devices(id)
    );

-- THE MAGIC COMMAND:
-- This converts the standard table above into a "Hypertable".
-- It effectively tells Postgres: "Partition this data by time automatically."
SELECT create_hypertable('sensor_readings', 'time', if_not_exists => TRUE);

-- Index for speed:
-- "Find me all Temperature readings for Device A, sorted by time."
CREATE INDEX IF NOT EXISTS idx_readings_device_time
ON sensor_readings (device_id, time DESC);

-- ==========================================
-- DDL: Alerts
-- ==========================================

CREATE TABLE IF NOT EXISTS alert_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sensor_type VARCHAR(50) NOT NULL,
    operator VARCHAR(10) NOT NULL, -- '>', '<', '>=', '<='
    threshold DOUBLE PRECISION NOT NULL,
    duration_seconds INT DEFAULT 0,
    severity VARCHAR(20) DEFAULT 'WARNING'
    );

CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID NOT NULL REFERENCES devices(id),
    rule_id UUID REFERENCES alert_rules(id),
    triggered_value DOUBLE PRECISION NOT NULL,
    triggered_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    severity VARCHAR(20) NOT NULL
    );