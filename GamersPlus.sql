-- Creamos la base de datos
CREATE DATABASE IF NOT EXISTS GamersPlus;
USE GamersPlus;

-- Si existen las tablas las eliminamos
DROP TABLE IF EXISTS DetalleIntercambio;
DROP TABLE IF EXISTS Intercambios;
DROP TABLE IF EXISTS ListaDeseados;
DROP TABLE IF EXISTS Videojuegos;
DROP TABLE IF EXISTS Usuarios;

-- Tabla de usuarios
CREATE TABLE Usuarios (
    ID_US INT AUTO_INCREMENT,
    Nom_US VARCHAR(50) NOT NULL,
    Apell_US VARCHAR(50) NOT NULL,
    Email_US VARCHAR(50) NOT NULL,
    Contraseña VARCHAR(100) NOT NULL,
    PRIMARY KEY (ID_US)
);

-- Tabla de videojuegos
CREATE TABLE Videojuegos (
    ID_VID INT AUTO_INCREMENT,
    Titulo VARCHAR(100) NOT NULL,
    ID_US INT NOT NULL,
    PRIMARY KEY (ID_VID),
    FOREIGN KEY (ID_US) REFERENCES Usuarios(ID_US)
);

-- Tabla de lista de deseados
CREATE TABLE ListaDeseados (
    ID_LISTA INT AUTO_INCREMENT,
    ID_US INT NOT NULL,
    ID_VID INT NOT NULL,
    PRIMARY KEY (ID_LISTA),
    FOREIGN KEY (ID_US) REFERENCES Usuarios(ID_US),
    FOREIGN KEY (ID_VID) REFERENCES Videojuegos(ID_VID)
);

-- Tabla de intercambios
CREATE TABLE Intercambios (
    ID_INT INT AUTO_INCREMENT,
    ID_US_INICIADOR INT NOT NULL,
    ID_US_DESTINATARIO INT NOT NULL,
    Fecha_Inicio DATETIME NOT NULL,
    Fecha_Resolucion DATETIME,
    Estado ENUM('pendiente', 'aceptado', 'rechazado', 'fallido', 'completado') NOT NULL,
    Es_Contrapropuesta BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (ID_INT),
    FOREIGN KEY (ID_US_INICIADOR) REFERENCES Usuarios(ID_US),
    FOREIGN KEY (ID_US_DESTINATARIO) REFERENCES Usuarios(ID_US)
);

-- Tabla detalle de intercambio
CREATE TABLE DetalleIntercambio (
    ID_DETALLE INT AUTO_INCREMENT,
    ID_INT INT NOT NULL,
    ID_US INT NOT NULL,
    ID_VID INT NOT NULL,
    PRIMARY KEY (ID_DETALLE),
    FOREIGN KEY (ID_INT) REFERENCES Intercambios(ID_INT),
    FOREIGN KEY (ID_US) REFERENCES Usuarios(ID_US),
    FOREIGN KEY (ID_VID) REFERENCES Videojuegos(ID_VID)
);

-- Inserción de datos en las tablas

-- Inserción de datos en la tabla Usuarios
INSERT INTO Usuarios (Nom_US, Apell_US, Email_US, Contraseña) VALUES
('Nahuel', 'Molina', 'nahuel_molina@gmail.com', '1234'),
('Gonzalo', 'Montiel', 'gonza_montiel@hotmail.com', 'abcd'),
('Emiliano', 'Martínez', 'emiliano.martinez@yahoo.com', 'qwerty');

-- Inserción de datos en la tabla Videojuegos
INSERT INTO Videojuegos (Titulo, ID_US) VALUES
('The Witcher 3', 1),
('Hades', 2),
('Cyberpunk 2077', 3),
('Stardew Valley', 1),
('Hollow Knight', 2);

-- Inserción de datos en la tabla ListaDeseados
INSERT INTO ListaDeseados (ID_US, ID_VID) VALUES
(1, 2),  -- Nahuel desea Hades
(2, 4),  -- Gonzalo desea Stardew Valley
(3, 1);  -- Emiliano desea The Witcher 3

-- Inserción de datos en la tabla Intercambios
INSERT INTO Intercambios (ID_US_INICIADOR, ID_US_DESTINATARIO, Fecha_Inicio, Fecha_Resolucion, Estado, Es_Contrapropuesta) VALUES
(1, 2, '2025-05-15 14:00:00', '2025-05-15 15:00:00', 'completado', FALSE),
(3, 1, '2025-05-16 10:30:00', NULL, 'pendiente', FALSE);

-- Inserción de datos en la tabla DetalleIntercambio

-- Intercambio 1: Nahuel (The Witcher 3) ↔ Gonzalo (Hades)

INSERT INTO DetalleIntercambio (ID_INT, ID_US, ID_VID) VALUES
(1, 1, 1),
(1, 2, 2);

-- Intercambio 2: Emiliano (Cyberpunk) ↔ Nahuel (Stardew Valley)

INSERT INTO DetalleIntercambio (ID_INT, ID_US, ID_VID) VALUES
(2, 3, 3),
(2, 1, 4);
