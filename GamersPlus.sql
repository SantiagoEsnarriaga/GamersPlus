CREATE DATABASE IF NOT EXISTS GamersPlus;
USE GamersPlus;

DROP TABLE IF EXISTS ListaVideojuegos;
DROP TABLE IF EXISTS ListasDeseados;
DROP TABLE IF EXISTS Intercambios;
DROP TABLE IF EXISTS Videojuegos;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS VideojuegosDeseados;

-- Tabla de usuarios
CREATE TABLE Usuarios (
    ID_US INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Contraseña VARCHAR(100) NOT NULL
);

-- Tabla de videojuegos
CREATE TABLE Videojuegos (
    ID_VID INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(100) NOT NULL,
    Genero VARCHAR(50) NOT NULL,
    Estado ENUM('DISPONIBLE','RESERVADO','INTERCAMBIADO') DEFAULT 'DISPONIBLE',
    ID_US_PROPIETARIO INT NOT NULL,
    FOREIGN KEY (ID_US_PROPIETARIO) REFERENCES Usuarios(ID_US) ON DELETE CASCADE
);

CREATE TABLE VideojuegosDeseados (
    ID_VID_DESEADO INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(100) NOT NULL,
    Genero VARCHAR(50) NOT NULL,
    -- Campos adicionales que el usuario pueda especificar
    Plataforma VARCHAR(50),
    Descripcion TEXT,
    -- Índice único para evitar duplicados del mismo juego
    UNIQUE KEY unique_game (Titulo, Genero, Plataforma)
);

-- Tabla de lista de deseados
CREATE TABLE ListasDeseados ( 
    ID_LISTA INT AUTO_INCREMENT PRIMARY KEY, 
    ID_USUARIO INT NOT NULL, 
    ID_VID_DESEADO INT NOT NULL,
    Fecha_Agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_game (ID_USUARIO, ID_VID_DESEADO),
    FOREIGN KEY (ID_USUARIO) REFERENCES Usuarios(ID_US) ON DELETE CASCADE,
    FOREIGN KEY (ID_VID_DESEADO) REFERENCES VideojuegosDeseados(ID_VID_DESEADO) ON DELETE CASCADE
); 

-- Tabla relación lista-videojuego
CREATE TABLE ListaVideojuegos (
    ID_LISTA INT NOT NULL,
    ID_VID INT NOT NULL,
    PRIMARY KEY (ID_LISTA, ID_VID),
    FOREIGN KEY (ID_LISTA) REFERENCES ListasDeseados(ID_LISTA) ON DELETE CASCADE,
    FOREIGN KEY (ID_VID) REFERENCES Videojuegos(ID_VID) ON DELETE CASCADE
);

-- Tabla de intercambios
CREATE TABLE Intercambios (
    ID_INT INT AUTO_INCREMENT PRIMARY KEY,
    ID_EMISOR INT NOT NULL,
    ID_RECEPTOR INT NOT NULL,
    ID_VID_OFERTADO INT NOT NULL,
    ID_VID_SOLICITADO INT NOT NULL,
    Fecha_Propuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Estado ENUM('PENDIENTE','ACEPTADO','RECHAZADO','COMPLETADO','FALLIDO','CONTRAPROPUESTO') DEFAULT 'PENDIENTE',
    Fecha_Resolucion TIMESTAMP NULL,
    ID_INTERCAMBIO_ORIGINAL INT NULL,
    ID_CONTRAPROPUESTA INT NULL,
    FOREIGN KEY (ID_EMISOR) REFERENCES Usuarios(ID_US) ON DELETE CASCADE,
    FOREIGN KEY (ID_RECEPTOR) REFERENCES Usuarios(ID_US) ON DELETE CASCADE,
    FOREIGN KEY (ID_VID_OFERTADO) REFERENCES Videojuegos(ID_VID) ON DELETE CASCADE,
    FOREIGN KEY (ID_VID_SOLICITADO) REFERENCES Videojuegos(ID_VID) ON DELETE CASCADE,
    FOREIGN KEY (ID_INTERCAMBIO_ORIGINAL) REFERENCES Intercambios(ID_INT) ON DELETE SET NULL,
    FOREIGN KEY (ID_CONTRAPROPUESTA) REFERENCES Intercambios(ID_INT) ON DELETE SET NULL
);


-- Índices para mejorar rendimiento
CREATE INDEX idx_videojuegos_usuario ON Videojuegos(ID_US_PROPIETARIO); 
CREATE INDEX idx_intercambios_emisor ON Intercambios(ID_EMISOR); 
CREATE INDEX idx_intercambios_receptor ON Intercambios(ID_RECEPTOR); 
CREATE INDEX idx_intercambios_estado ON Intercambios(Estado);
CREATE INDEX idx_lista_deseados_usuario ON ListasDeseados(ID_USUARIO);



-- ========================================
-- EJEMPLOS DE INSERCIONES DE DATOS
-- ========================================

-- Usuarios
INSERT INTO Usuarios (Nombre, Email, Contraseña) VALUES
('Nahuel Molina', 'nahuel_molina@gmail.com', '1234'),
('Gonzalo Montiel', 'gonza_montiel@hotmail.com', 'abcd'),
('Emiliano Martínez', 'emiliano.martinez@yahoo.com', 'qwerty');

-- Videojuegos
INSERT INTO Videojuegos (Titulo, Genero, Estado, ID_US_PROPIETARIO) VALUES
('The Witcher 3', 'RPG', 'DISPONIBLE', 1),
('Hades', 'Roguelike', 'DISPONIBLE', 2),
('Cyberpunk 2077', 'RPG', 'DISPONIBLE', 3),
('Stardew Valley', 'Simulación', 'DISPONIBLE', 1),
('Hollow Knight', 'Metroidvania', 'DISPONIBLE', 2);

-- Videojuegos Deseados
INSERT INTO VideojuegosDeseados (Titulo, Genero, Plataforma, Descripcion) VALUES
('Elden Ring Nightreign', 'RPG', 'PC', 'Último juego de FromSoftware'),
('God of War', 'Acción', 'PC', 'Juego clásico de Kratos'),
('Silent Hill 2', 'Horror', 'PC', 'Juego de horror psicológico'),
('Halo Infinite', 'FPS', 'PC', 'Shooter de Microsoft'),
('Call of Duty: Black Ops 7', 'FPS', 'PC', 'Último juego de Activision');

-- Listas de deseados
INSERT INTO ListasDeseados (ID_USUARIO, ID_VID_DESEADO) VALUES
(1, 1),  -- Nahuel desea Elden Ring Nightreign
(1, 2),  -- Nahuel desea God of War
(2, 3),  -- Gonzalo desea Silent Hill 2
(2, 5),  -- Gonzalo desea Call of Duty: Black Ops 7
(3, 1),  -- Emiliano desea Elden Ring
(3, 4);  -- Emiliano desea Halo Infinite

-- Intercambios
INSERT INTO Intercambios (
    ID_EMISOR, 
    ID_RECEPTOR, 
    ID_VID_OFERTADO, 
    ID_VID_SOLICITADO, 
    Fecha_Propuesta, 
    Fecha_Resolucion, 
    Estado
) VALUES
(1, 2, 1, 2, '2025-05-15 14:00:00', '2025-05-15 15:00:00', 'COMPLETADO'),
(3, 1, 3, 4, '2025-05-16 10:30:00', NULL, 'PENDIENTE');


-- ========================================
-- CONSULTAS PRINCIPALES DEL SISTEMA
-- ========================================


-- 1. Obtener lista de todos los usuarios registrados
SELECT ID_US, Nombre, Email FROM Usuarios;

-- 2. Obtener bibliotecas de todos los usuarios
SELECT 
    v.ID_VID,
    v.Titulo,
    v.Genero,
    u.Nombre AS Propietario
FROM Videojuegos v
JOIN Usuarios u ON v.ID_US_PROPIETARIO = u.ID_US;

-- 3. Obtener lista de deseados de un usuario
SELECT 
    vd.ID_VID_DESEADO,
    vd.Titulo,
    vd.Genero,
    ld.Fecha_Agregado
FROM VideojuegosDeseados vd
JOIN ListasDeseados ld ON vd.ID_VID_DESEADO = ld.ID_VID_DESEADO
WHERE ld.ID_USUARIO = 1
ORDER BY ld.Fecha_Agregado DESC;

-- 4. Obtener intercambios activos de un usuario
SELECT 
    ID_INT,
    ID_EMISOR,
    ID_RECEPTOR,
    ID_VID_OFERTADO,
    ID_VID_SOLICITADO,
    Fecha_Propuesta,
    Estado
FROM Intercambios 
WHERE (ID_EMISOR = 1 OR ID_RECEPTOR = 1)
AND Estado = 'PENDIENTE'
ORDER BY Fecha_Propuesta DESC;

-- 5. Obtener detalles de intercambio con nombres de juegos
SELECT 
    i.ID_INT,
    e.Nombre AS Emisor,
    r.Nombre AS Receptor,
    vo.Titulo AS Juego_Ofertado,
    vs.Titulo AS Juego_Solicitado,
    i.Fecha_Propuesta,
    i.Fecha_Resolucion,
    i.Estado
FROM Intercambios i
JOIN Usuarios e ON i.ID_EMISOR = e.ID_US
JOIN Usuarios r ON i.ID_RECEPTOR = r.ID_US
JOIN Videojuegos vo ON i.ID_VID_OFERTADO = vo.ID_VID
JOIN Videojuegos vs ON i.ID_VID_SOLICITADO = vs.ID_VID
ORDER BY i.Fecha_Propuesta DESC;


-- 6. Búsqueda de coincidencias: Encontrar usuarios que tengan juegos que yo deseo
SELECT DISTINCT
    u.ID_US,
    u.Nombre AS Usuario_Propietario,
    v.ID_VID,
    v.Titulo AS Juego_Disponible,
    vd.Titulo AS Juego_Deseado
FROM Usuarios u
JOIN Videojuegos v ON u.ID_US = v.ID_US_PROPIETARIO
JOIN VideojuegosDeseados vd ON v.Titulo = vd.Titulo
JOIN ListasDeseados ld ON vd.ID_VID_DESEADO = ld.ID_VID_DESEADO
WHERE ld.ID_USUARIO = 1  -- ID del usuario que busca coincidencias
AND v.Estado = 'DISPONIBLE'
AND u.ID_US != 1;  -- Excluir al propio usuario

-- 7. Estadísticas del usuario: Información general del perfil
SELECT 
    u.Nombre,
    COUNT(DISTINCT v.ID_VID) AS Total_Juegos_Propios,
    COUNT(DISTINCT ld.ID_VID_DESEADO) AS Total_Juegos_Deseados,
    COUNT(DISTINCT CASE WHEN i.Estado = 'COMPLETADO' THEN i.ID_INT END) AS Intercambios_Completados,
    COUNT(DISTINCT CASE WHEN i.Estado = 'PENDIENTE' THEN i.ID_INT END) AS Intercambios_Pendientes
FROM Usuarios u
LEFT JOIN Videojuegos v ON u.ID_US = v.ID_US_PROPIETARIO
LEFT JOIN ListasDeseados ld ON u.ID_US = ld.ID_USUARIO
LEFT JOIN Intercambios i ON u.ID_US = i.ID_EMISOR OR u.ID_US = i.ID_RECEPTOR
WHERE u.ID_US = 1
GROUP BY u.ID_US, u.Nombre;

