CREATE TABLE categorias (
                            id              BIGINT          NOT NULL AUTO_INCREMENT,
                            fecha_alta      DATETIME(6)     DEFAULT NULL,
                            fecha_baja      DATETIME(6)     DEFAULT NULL,
                            fecha_modificacion DATETIME(6)  DEFAULT NULL,
                            descripcion     VARCHAR(255)    DEFAULT NULL,
                            nombre          VARCHAR(100)    NOT NULL,
                            PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE servicios (
                           id              BIGINT          NOT NULL AUTO_INCREMENT,
                           fecha_alta      DATETIME(6)     DEFAULT NULL,
                           fecha_baja      DATETIME(6)     DEFAULT NULL,
                           fecha_modificacion DATETIME(6)  DEFAULT NULL,
                           descripcion     VARCHAR(255)    DEFAULT NULL,
                           duracion        INT             NOT NULL,
                           nombre          VARCHAR(100)    NOT NULL,
                           categoria_id    BIGINT          NOT NULL,
                           PRIMARY KEY (id),
                           CONSTRAINT fk_servicios_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE horarios (
                          id              BIGINT          NOT NULL AUTO_INCREMENT,
                          fecha_alta      DATETIME(6)     DEFAULT NULL,
                          fecha_baja      DATETIME(6)     DEFAULT NULL,
                          fecha_modificacion DATETIME(6)  DEFAULT NULL,
                          dia_semana      ENUM('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') NOT NULL,
                          hora_fin        TIME            NOT NULL,
                          hora_inicio     TIME            NOT NULL,
                          servicio_id     BIGINT          NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_horarios_servicio FOREIGN KEY (servicio_id) REFERENCES servicios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE turnos (
                        id              BIGINT          NOT NULL AUTO_INCREMENT,
                        fecha_alta      DATETIME(6)     DEFAULT NULL,
                        fecha_baja      DATETIME(6)     DEFAULT NULL,
                        fecha_modificacion DATETIME(6)  DEFAULT NULL,
                        fecha           DATE            NOT NULL,
                        hora_fin        TIME            NOT NULL,
                        hora_inicio     TIME            NOT NULL,
                        horario_id      BIGINT          NOT NULL,
                        PRIMARY KEY (id),
                        CONSTRAINT fk_turnos_horario FOREIGN KEY (horario_id) REFERENCES horarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE usuarios (
                          id              BIGINT          NOT NULL AUTO_INCREMENT,
                          fecha_alta      DATETIME(6)     DEFAULT NULL,
                          fecha_baja      DATETIME(6)     DEFAULT NULL,
                          fecha_modificacion DATETIME(6)  DEFAULT NULL,
                          apellido        VARCHAR(255)    NOT NULL,
                          contrasenia     VARCHAR(255)    NOT NULL,
                          mail            VARCHAR(255)    NOT NULL,
                          nombre          VARCHAR(255)    NOT NULL,
                          rol             ENUM('ADMIN','CLIENTE','EMPLEADO') NOT NULL,
                          telefono        VARCHAR(255)    DEFAULT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_usuarios_mail (mail)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE reservas (
                          id                  BIGINT      NOT NULL AUTO_INCREMENT,
                          fecha_alta          DATETIME(6) DEFAULT NULL,
                          fecha_baja          DATETIME(6) DEFAULT NULL,
                          fecha_modificacion  DATETIME(6) DEFAULT NULL,
                          estado              ENUM('ACTIVE','CANCELLED','COMPLETED') NOT NULL,
                          motivo_cancelacion  ENUM('CAMBIO_DE_PLANES','EMERGENCIA','ERROR_AL_RESERVAR','OTRO') DEFAULT NULL,
                          servicio_id         BIGINT      NOT NULL,
                          turno_id            BIGINT      NOT NULL,
                          usuario_id          BIGINT      NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_reservas_servicio FOREIGN KEY (servicio_id) REFERENCES servicios (id),
                          CONSTRAINT fk_reservas_turno    FOREIGN KEY (turno_id)    REFERENCES turnos (id),
                          CONSTRAINT fk_reservas_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;