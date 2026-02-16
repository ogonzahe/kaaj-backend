/*M!999999\- enable the sandbox mode */

-- ============================================================
-- WARNING: This file is for DEVELOPMENT/TESTING purposes only.
-- Do NOT use this data in production environments.
-- All passwords should be hashed with BCrypt before deployment.
-- ============================================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;
DROP TABLE IF EXISTS `acceso_seguridad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `acceso_seguridad` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creado_en` datetime(6) DEFAULT NULL,
  `fecha_hora_acceso` datetime(6) NOT NULL,
  `guardia_id` int(11) DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `tipo_acceso` varchar(255) DEFAULT NULL,
  `visita_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK81cct65l09y6cgs4e4pvibe87` (`visita_id`),
  CONSTRAINT `FK81cct65l09y6cgs4e4pvibe87` FOREIGN KEY (`visita_id`) REFERENCES `visitas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `acceso_seguridad` WRITE;
/*!40000 ALTER TABLE `acceso_seguridad` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `acceso_seguridad` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `administrador_condominios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador_condominios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `condominio_id` int(11) NOT NULL,
  `es_principal` tinyint(1) DEFAULT 0,
  `fecha_asignacion` timestamp NULL DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_admin_cond` (`usuario_id`,`condominio_id`),
  KEY `condominio_id` (`condominio_id`),
  CONSTRAINT `administrador_condominios_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `administrador_condominios_ibfk_2` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `administrador_condominios` WRITE;
/*!40000 ALTER TABLE `administrador_condominios` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `administrador_condominios` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `amenidades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `amenidades` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `capacidad` int(11) DEFAULT 1,
  `estado` enum('activa','inactiva') DEFAULT 'activa',
  `creado_en` timestamp NULL DEFAULT current_timestamp(),
  `condominio_id` int(11) DEFAULT NULL,
  `hora_apertura` time DEFAULT '08:00:00',
  `hora_cierre` time DEFAULT '22:00:00',
  `dias_disponibles` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '["lunes","martes","miercoles","jueves","viernes","sabado","domingo"]' CHECK (json_valid(`dias_disponibles`)),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_nombre_condominio` (`nombre`,`condominio_id`),
  KEY `fk_amenidad_condominio` (`condominio_id`),
  CONSTRAINT `fk_amenidad_condominio` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `amenidades` WRITE;
/*!40000 ALTER TABLE `amenidades` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `amenidades` (`id`, `nombre`, `descripcion`, `capacidad`, `estado`, `creado_en`, `condominio_id`, `hora_apertura`, `hora_cierre`, `dias_disponibles`) VALUES (15,'sala de juegos','juegos',15,'activa','2026-01-20 01:31:18',8,'11:00:00','20:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\"]'),
(16,'asador','para la carnita asada',10,'activa','2026-01-20 01:40:52',7,'12:00:00','22:00:00','[\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(17,'ALBERCA','ALBERCA',1,'activa','2026-01-23 01:55:16',10,'08:00:00','21:00:00','[\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(18,'GIMNASIO','GIMNASIO',1,'activa','2026-01-23 01:56:24',10,'07:00:00','22:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(20,'ASADOR ZANTE','ASADOR',5,'inactiva','2026-01-23 01:57:33',10,'08:00:00','22:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(22,'sala de juegos','sala de juegos',3,'activa','2026-01-28 03:27:33',7,'08:00:00','22:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\"]'),
(23,'SPA','spa',1,'activa','2026-01-28 03:27:51',8,'08:00:00','22:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(24,'gym','uso',1,'inactiva','2026-02-03 05:37:56',8,'12:09:00','09:09:00','[\"lunes\",\"martes\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(25,'gym','',1,'activa','2026-02-03 05:38:26',7,'12:00:00','22:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(26,'PADEL','padel',2,'activa','2026-02-04 01:37:35',13,'08:00:00','22:00:00','[\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(27,'ALBERCA','alberca',10,'activa','2026-02-04 01:38:25',13,'08:00:00','22:00:00','[\"lunes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]'),
(28,'ASADOR','asador',2,'activa','2026-02-04 01:38:49',13,'08:00:00','20:00:00','[\"lunes\",\"martes\",\"miercoles\",\"jueves\",\"viernes\",\"sabado\",\"domingo\"]');
/*!40000 ALTER TABLE `amenidades` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `apartamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `apartamentos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `condominio_id` int(11) NOT NULL,
  `edificio` varchar(50) NOT NULL,
  `numero` varchar(20) NOT NULL,
  `tipo` varchar(50) DEFAULT 'Departamento',
  `area` decimal(38,2) DEFAULT NULL,
  `habitaciones` int(11) DEFAULT 2,
  `banos` int(11) DEFAULT 1,
  `propietario_usuario_id` int(11) DEFAULT NULL,
  `inquilino_usuario_id` int(11) DEFAULT NULL,
  `esta_alquilado` tinyint(1) DEFAULT 0,
  `esta_habitado` tinyint(1) DEFAULT 1,
  `cuota_mantenimiento_base` decimal(38,2) DEFAULT NULL,
  `observaciones` longtext DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_apartamento_condominio` (`condominio_id`,`edificio`,`numero`),
  KEY `idx_apartamento_condominio` (`condominio_id`),
  KEY `idx_apartamento_propietario` (`propietario_usuario_id`),
  KEY `idx_apartamento_inquilino` (`inquilino_usuario_id`),
  CONSTRAINT `apartamentos_ibfk_1` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `apartamentos_ibfk_2` FOREIGN KEY (`propietario_usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `apartamentos_ibfk_3` FOREIGN KEY (`inquilino_usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `apartamentos` WRITE;
/*!40000 ALTER TABLE `apartamentos` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `apartamentos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `categorias_documento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_documento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `color` varchar(7) DEFAULT NULL,
  `condominio_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categoria_nombre_condominio` (`condominio_id`,`nombre`),
  UNIQUE KEY `UK1bmeraoglysbqgvg2hxbn0dpo` (`condominio_id`,`nombre`),
  CONSTRAINT `FK19s003k0cqfsw03ooemsnjfld` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `categorias_documento` WRITE;
/*!40000 ALTER TABLE `categorias_documento` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `categorias_documento` (`id`, `nombre`, `descripcion`, `color`, `condominio_id`) VALUES (3,'Reglamento',NULL,'#81132f',7),
(6,'copo',NULL,'#956a0f',10),
(7,'Actas','Actas de reuniones','#10b981',7),
(8,'Finanzas','Documentos financieros','#f59e0b',7),
(16,'oye',NULL,'#3b82f6',8),
(17,'oye',NULL,'#3b82f6',9),
(19,'Reglamento',NULL,'#f73b3b',13);
/*!40000 ALTER TABLE `categorias_documento` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `categorias_egresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_egresos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `condominio_id` (`condominio_id`),
  CONSTRAINT `categorias_egresos_ibfk_1` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `categorias_egresos` WRITE;
/*!40000 ALTER TABLE `categorias_egresos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `categorias_egresos` (`id`, `nombre`, `descripcion`, `color`, `condominio_id`, `created_at`, `updated_at`) VALUES (1,'Sueldos Personal','Pago de salarios del personal','#F44336',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(2,'Mantenimiento Edificio','Mantenimiento general del edificio','#E53935',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(3,'Servicios Públicos','Agua, luz, gas, internet','#D32F2F',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(4,'Limpieza','Servicios de limpieza','#C62828',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(5,'Seguridad','Servicios de vigilancia','#B71C1C',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(6,'Administración','Gastos administrativos','#FF5252',7,'2026-01-29 04:28:33','2026-01-29 04:28:33');
/*!40000 ALTER TABLE `categorias_egresos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `categorias_ingresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_ingresos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `condominio_id` (`condominio_id`),
  CONSTRAINT `categorias_ingresos_ibfk_1` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `categorias_ingresos` WRITE;
/*!40000 ALTER TABLE `categorias_ingresos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `categorias_ingresos` (`id`, `nombre`, `descripcion`, `color`, `condominio_id`, `created_at`, `updated_at`) VALUES (1,'Mantenimiento Mensual','Cuotas mensuales de mantenimiento','#4CAF50',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(2,'Multas','Multas por infracciones','#45a049',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(3,'Renta Amenidades','Renta de áreas comunes','#388E3C',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(4,'Fondo Reserva','Aportaciones al fondo de reserva','#2E7D32',7,'2026-01-29 04:28:33','2026-01-29 04:28:33'),
(5,'Otros Ingresos','Otros ingresos varios','#1B5E20',7,'2026-01-29 04:28:33','2026-01-29 04:28:33');
/*!40000 ALTER TABLE `categorias_ingresos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `condominios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `condominios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `creado_en` timestamp NULL DEFAULT current_timestamp(),
  `entidad` varchar(255) DEFAULT NULL,
  `numero_casas` int(11) DEFAULT NULL,
  `responsable` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `administracion` varchar(255) DEFAULT NULL,
  `ciudad` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `condominios` WRITE;
/*!40000 ALTER TABLE `condominios` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `condominios` (`id`, `nombre`, `direccion`, `creado_en`, `entidad`, `numero_casas`, `responsable`, `telefono`, `email`, `administracion`, `ciudad`, `codigo_postal`, `activo`) VALUES (7,'ARGU II','del otro lado','2026-01-14 18:16:30','MORELOS',13,'Paolina','1233445566','cesar@ejemplo.com','copo','morelos -creo-',NULL,1),
(8,'ARGU III','pase de alcatraces 700','2026-01-19 19:28:31','QUERETARO',5,'PAOLINA CRUZ','1111111111','paulina@mail.com','hom s.a de c.v','queretaro',NULL,1),
(9,'RISCOS','QUERETARO','2026-01-22 19:41:10','QUERETARO',5,'RICARDO','12345678','riscos@mail.com','HOME','QUERETARO',NULL,1),
(10,'ZANTE','QUERETARO','2026-01-22 19:42:27','QUERETARO',3,'JOSE','87654321','jose@mail.com','JOME','QUERETARO',NULL,1),
(13,'PETREL','QUERETARO','2026-02-04 01:17:54','QUERETARO',5,'ROBERTO','5624164755','roberto@kaaj.com','PETREL','QUERETARO',NULL,1),
(14,'REFUGIO','QUERETARO','2026-02-04 01:25:26','QUERETARO',2,'ROBERTO','2321323123','roberto@mail.com','PETREL','QUERETARO',NULL,1);
/*!40000 ALTER TABLE `condominios` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `documentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `nombre_archivo` varchar(255) DEFAULT NULL,
  `ruta_archivo` varchar(255) DEFAULT NULL,
  `tamanio` bigint(20) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `categoria_id` bigint(20) DEFAULT NULL,
  `es_publico` tinyint(1) DEFAULT 1,
  `fecha_vigencia` date DEFAULT NULL,
  `creado_en` datetime DEFAULT current_timestamp(),
  `creado_por` int(11) DEFAULT NULL,
  `actualizado_en` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `condominio_id` (`condominio_id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `creado_por` (`creado_por`),
  CONSTRAINT `documentos_ibfk_1` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `documentos_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_documento` (`id`) ON DELETE SET NULL,
  CONSTRAINT `documentos_ibfk_3` FOREIGN KEY (`creado_por`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `documentos` WRITE;
/*!40000 ALTER TABLE `documentos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `documentos` (`id`, `titulo`, `descripcion`, `nombre_archivo`, `ruta_archivo`, `tamanio`, `mime_type`, `condominio_id`, `categoria_id`, `es_publico`, `fecha_vigencia`, `creado_en`, `creado_por`, `actualizado_en`) VALUES (1,'reglas','reglas','1768454225541_f0f87c3a-3d2e-46d3-ae42-0bd7951eadb2.pdf','uploads/documents/1768454225541_f0f87c3a-3d2e-46d3-ae42-0bd7951eadb2.pdf',66763,'application/pdf',NULL,NULL,1,NULL,'2026-01-14 23:17:05',14,'2026-01-14 23:17:05'),
(2,'d','d','f0f87c3a-3d2e-46d3-ae42-0bd7951eadb2.pdf','uploads/documents/b2c5d2d7-b1a5-465c-9029-9ebf5b7b10d7.pdf',66763,'application/pdf',NULL,NULL,1,NULL,'2026-01-15 00:40:19',14,'2026-01-15 00:40:19'),
(3,'a','a','ine2.jpeg','uploads/documents/3c211da9-417c-4ede-9f31-e28ea37308cb.jpeg',256696,'image/jpeg',NULL,NULL,1,NULL,'2026-01-15 01:11:12',14,'2026-01-15 01:11:12'),
(4,'2','1','logo.png','uploads/documents/4b653991-b4f9-4718-8ba7-9e5f633e2ae7.png',176151,'image/png',NULL,NULL,1,NULL,'2026-01-15 12:25:34',14,'2026-01-15 12:25:34'),
(21,'Notas sobre el hogar','una prueba','ventcas.pdf','/uploads/39dbc72d-1681-4ba1-9032-bd4ad446326d.pdf',8396222,'application/pdf',7,3,1,'2026-02-26','2026-02-02 21:54:59',14,'2026-02-02 21:54:59'),
(22,'Reglaento','reglas','AREASDIF.xlsx','/uploads/2bccac78-b2b9-4cfd-b5c6-69c76061b4fd.xlsx',14143,'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',13,19,1,NULL,'2026-02-04 02:23:41',31,'2026-02-04 02:23:41');
/*!40000 ALTER TABLE `documentos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `egresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `egresos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `concepto` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `categoria_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) NOT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `fecha` date NOT NULL,
  `mes` int(11) NOT NULL,
  `año` int(11) NOT NULL,
  `comprobante_url` varchar(500) DEFAULT NULL,
  `estatus` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `condominio_id` (`condominio_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `egresos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_egresos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `egresos_ibfk_2` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `egresos_ibfk_3` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `egresos` WRITE;
/*!40000 ALTER TABLE `egresos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `egresos` (`id`, `concepto`, `descripcion`, `monto`, `categoria_id`, `condominio_id`, `usuario_id`, `fecha`, `mes`, `año`, `comprobante_url`, `estatus`, `created_at`, `updated_at`) VALUES (21,'Sueldo conserje enero 2025','Sueldo mensual conserje',8500.00,1,7,14,'2025-01-05',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(22,'Luz áreas comunes enero 2025','Factura CFE áreas comunes',12500.00,3,7,14,'2025-01-10',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(23,'Agua edificio enero 2025','Factura SACMEX consumo agua',4500.00,3,7,14,'2025-01-12',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(24,'Mantenimiento ascensor','Mantenimiento preventivo ascensor',2800.00,2,7,14,'2025-01-15',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(25,'Material limpieza','Compra productos limpieza',2200.00,4,7,14,'2025-01-18',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(26,'Sueldo vigilante enero 2025','Sueldo vigilante turno nocturno',9500.00,5,7,14,'2025-01-20',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(27,'Alarma y CCTV','Mantenimiento sistema seguridad',1800.00,5,7,14,'2025-01-22',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(28,'Papelería administración','Material oficina y papelería',1200.00,6,7,14,'2025-01-25',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(29,'Sueldo conserje febrero 2025','Sueldo mensual conserje',8500.00,1,7,14,'2025-02-05',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(30,'Luz áreas comunes febrero 2025','Factura CFE áreas comunes',11800.00,3,7,14,'2025-02-10',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(31,'Agua edificio febrero 2025','Factura SACMEX consumo agua',4200.00,3,7,14,'2025-02-12',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(32,'Reparación puerta principal','Reparación vidrio puerta principal',3800.00,2,7,14,'2025-02-15',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(33,'Sueldo conserje enero 2024','Sueldo mensual conserje',8000.00,1,7,14,'2024-01-05',1,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(34,'Luz áreas comunes enero 2024','Factura CFE áreas comunes',11500.00,3,7,14,'2024-01-10',1,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(35,'Agua edificio enero 2024','Factura SACMEX consumo agua',4200.00,3,7,14,'2024-01-12',1,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(36,'Mantenimiento ascensor 2024','Mantenimiento preventivo',2500.00,2,7,14,'2024-01-15',1,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(37,'Sueldo conserje febrero 2024','Sueldo mensual conserje',8000.00,1,7,14,'2024-02-05',2,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(38,'Luz áreas comunes febrero 2024','Factura CFE áreas comunes',11200.00,3,7,14,'2024-02-10',2,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(39,'Sueldo conserje enero 2023','Sueldo mensual conserje',7500.00,1,7,14,'2023-01-05',1,2023,NULL,'pagado','2026-01-29 04:34:54','2026-01-29 04:34:54'),
(40,'Luz áreas comunes enero 2023','Factura CFE áreas comunes',10500.00,3,7,14,'2023-01-10',1,2023,NULL,'pagado','2026-01-29 04:34:54','2026-01-29 04:34:54'),
(41,'Agua edificio enero 2023','Factura SACMEX consumo agua',3800.00,3,7,14,'2023-01-12',1,2023,NULL,'pagado','2026-01-29 04:34:54','2026-01-29 04:34:54'),
(42,'Mantenimiento ascensor 2023','Mantenimiento preventivo',2200.00,2,7,14,'2023-01-15',1,2023,NULL,'pagado','2026-01-29 04:34:54','2026-01-29 04:34:54');
/*!40000 ALTER TABLE `egresos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `escaneos_qr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `escaneos_qr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `visita_id` int(11) NOT NULL,
  `fecha_escaneo` datetime DEFAULT current_timestamp(),
  `dispositivo` varchar(255) DEFAULT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `visita_id` (`visita_id`),
  CONSTRAINT `escaneos_qr_ibfk_1` FOREIGN KEY (`visita_id`) REFERENCES `visitas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `escaneos_qr` WRITE;
/*!40000 ALTER TABLE `escaneos_qr` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `escaneos_qr` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `estatus_mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `estatus_mantenimiento` (
  `Id_Estatus` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) NOT NULL,
  PRIMARY KEY (`Id_Estatus`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `estatus_mantenimiento` WRITE;
/*!40000 ALTER TABLE `estatus_mantenimiento` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `estatus_mantenimiento` (`Id_Estatus`, `descripcion`) VALUES (1,'Pendiente'),
(2,'Resuelto'),
(3,'Cancelado');
/*!40000 ALTER TABLE `estatus_mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `ingresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingresos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `concepto` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `categoria_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) NOT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `fecha` date NOT NULL,
  `mes` int(11) NOT NULL,
  `año` int(11) NOT NULL,
  `comprobante_url` varchar(500) DEFAULT NULL,
  `estatus` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `condominio_id` (`condominio_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `ingresos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_ingresos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `ingresos_ibfk_2` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ingresos_ibfk_3` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `ingresos` WRITE;
/*!40000 ALTER TABLE `ingresos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `ingresos` (`id`, `concepto`, `descripcion`, `monto`, `categoria_id`, `condominio_id`, `usuario_id`, `fecha`, `mes`, `año`, `comprobante_url`, `estatus`, `created_at`, `updated_at`) VALUES (35,'Mantenimiento Enero 2025','Cuota mensual mantenimiento',50000.00,1,7,14,'2025-01-15',1,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(36,'Mantenimiento Febrero 2025','Cuota mensual mantenimiento',52000.00,1,7,14,'2025-02-15',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(37,'Multa por mascota','Multa por mascota sin correa',500.00,2,7,14,'2025-02-20',2,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(38,'Mantenimiento Marzo 2025','Cuota mensual mantenimiento',51000.00,1,7,14,'2025-03-15',3,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(39,'Renta salon eventos','Renta de salón de eventos',3000.00,3,7,14,'2025-03-20',3,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(40,'Mantenimiento Abril 2025','Cuota mensual mantenimiento',53000.00,1,7,14,'2025-04-15',4,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(41,'Fondo reserva','Aportación al fondo de reserva',10000.00,4,7,14,'2025-04-20',4,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(42,'Mantenimiento Mayo 2025','Cuota mensual mantenimiento',52500.00,1,7,14,'2025-05-15',5,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(43,'Multa estacionamiento','Multa por estacionamiento indebido',800.00,2,7,14,'2025-05-18',5,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(44,'Mantenimiento Junio 2025','Cuota mensual mantenimiento',51500.00,1,7,14,'2025-06-15',6,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(45,'Renta alberca','Renta de alberca para evento',2500.00,3,7,14,'2025-06-22',6,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(46,'Mantenimiento Julio 2025','Cuota mensual mantenimiento',54000.00,1,7,14,'2025-07-15',7,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(47,'Fondo reserva','Aportación al fondo de reserva',10000.00,4,7,14,'2025-07-20',7,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(48,'Mantenimiento Agosto 2025','Cuota mensual mantenimiento',53500.00,1,7,14,'2025-08-15',8,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(49,'Multa ruido','Multa por ruido excesivo',600.00,2,7,14,'2025-08-19',8,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(50,'Mantenimiento Septiembre 2025','Cuota mensual mantenimiento',52000.00,1,7,14,'2025-09-15',9,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(51,'Renta gimnasio','Renta de gimnasio privado',2000.00,3,7,14,'2025-09-25',9,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(52,'Mantenimiento Octubre 2025','Cuota mensual mantenimiento',52500.00,1,7,14,'2025-10-15',10,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(53,'Fondo reserva','Aportación al fondo de reserva',10000.00,4,7,14,'2025-10-20',10,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(54,'Mantenimiento Noviembre 2025','Cuota mensual mantenimiento',53000.00,1,7,14,'2025-11-15',11,2025,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(55,'Mantenimiento Enero 2024','Cuota mensual mantenimiento',48000.00,1,7,14,'2024-01-15',1,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(56,'Mantenimiento Febrero 2024','Cuota mensual mantenimiento',49000.00,1,7,14,'2024-02-15',2,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(57,'Multa por área común','Multa por uso indebido de área común',550.00,2,7,14,'2024-02-20',2,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(58,'Mantenimiento Marzo 2024','Cuota mensual mantenimiento',49500.00,1,7,14,'2024-03-15',3,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(59,'Renta salón bodas','Renta salón para celebración de boda',2800.00,3,7,14,'2024-03-20',3,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(60,'Mantenimiento Abril 2024','Cuota mensual mantenimiento',50000.00,1,7,14,'2024-04-15',4,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(61,'Fondo reserva','Aportación al fondo de reserva',9500.00,4,7,14,'2024-04-20',4,2024,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(62,'Mantenimiento Enero 2023','Cuota mensual mantenimiento',45000.00,1,7,14,'2023-01-15',1,2023,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(63,'Mantenimiento Febrero 2023','Cuota mensual mantenimiento',46000.00,1,7,14,'2023-02-15',2,2023,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(64,'Multa mascota sin correa','Multa a departamento 4 por mascota sin correa',450.00,2,7,14,'2023-02-20',2,2023,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(65,'Mantenimiento Marzo 2023','Cuota mensual mantenimiento',47000.00,1,7,14,'2023-03-15',3,2023,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(66,'Renta salón eventos','Renta salón de eventos para fiesta cumpleaños',2500.00,3,7,14,'2023-03-20',3,2023,NULL,'pagado','2026-01-29 04:34:53','2026-01-29 04:34:53'),
(67,'Mantenimiento Abril 2025','Cuota mensual mantenimiento',55000.00,1,7,14,'2025-04-15',4,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(68,'Multa por estacionamiento','Multa por estacionamiento en área prohibida',800.00,2,7,14,'2025-04-20',4,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(69,'Renta salon fiestas','Renta de salón para fiesta de cumpleaños',2500.00,3,7,14,'2025-04-25',4,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(70,'Mantenimiento Mayo 2025','Cuota mensual mantenimiento',56000.00,1,7,14,'2025-05-15',5,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(71,'Fondo de reserva','Aportación al fondo de reserva',10000.00,4,7,14,'2025-05-20',5,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(72,'Mantenimiento Junio 2025','Cuota mensual mantenimiento',57000.00,1,7,14,'2025-06-15',6,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(73,'Multa por ruido','Multa por ruido excesivo después de las 10 PM',1200.00,2,7,14,'2025-06-20',6,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(74,'Renta cancha tenis','Renta de cancha de tenis por 2 horas',800.00,3,7,14,'2025-06-25',6,2025,NULL,'pagado','2026-02-05 04:35:26','2026-02-05 04:35:26'),
(75,'Mantenimiento Abril 2025','Cuota mensual mantenimiento',55000.00,1,7,14,'2025-04-15',4,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(76,'Multa por estacionamiento','Multa por estacionamiento en área prohibida',800.00,2,7,14,'2025-04-20',4,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(77,'Renta salon fiestas','Renta de salón para fiesta de cumpleaños',2500.00,3,7,14,'2025-04-25',4,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(78,'Mantenimiento Mayo 2025','Cuota mensual mantenimiento',56000.00,1,7,14,'2025-05-15',5,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(79,'Fondo de reserva','Aportación al fondo de reserva',10000.00,4,7,14,'2025-05-20',5,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(80,'Mantenimiento Junio 2025','Cuota mensual mantenimiento',57000.00,1,7,14,'2025-06-15',6,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(81,'Multa por ruido','Multa por ruido excesivo después de las 10 PM',1200.00,2,7,14,'2025-06-20',6,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41'),
(82,'Renta cancha tenis','Renta de cancha de tenis por 2 horas',800.00,3,7,14,'2025-06-25',6,2025,NULL,'pagado','2026-02-05 04:44:41','2026-02-05 04:44:41');
/*!40000 ALTER TABLE `ingresos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento` (
  `id_mantenimiento` int(11) NOT NULL AUTO_INCREMENT,
  `titulo_reporte` varchar(60) NOT NULL,
  `usuario_apartamento` varchar(100) NOT NULL DEFAULT '',
  `mensaje` varchar(500) NOT NULL DEFAULT '',
  `id_tipo` int(11) NOT NULL,
  `id_estatus` int(11) NOT NULL,
  `fecha_alta` datetime NOT NULL,
  `fecha_mod` datetime DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `ubicacion` varchar(100) DEFAULT NULL,
  `numero_casa` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_mantenimiento`),
  KEY `id_tipo` (`id_tipo`),
  KEY `id_estatus` (`id_estatus`),
  CONSTRAINT `mantenimiento_ibfk_1` FOREIGN KEY (`id_tipo`) REFERENCES `tipo_mantenimiento` (`id_tipo`),
  CONSTRAINT `mantenimiento_ibfk_2` FOREIGN KEY (`id_estatus`) REFERENCES `estatus_mantenimiento` (`Id_Estatus`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `mantenimiento` WRITE;
/*!40000 ALTER TABLE `mantenimiento` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `mantenimiento` (`id_mantenimiento`, `titulo_reporte`, `usuario_apartamento`, `mensaje`, `id_tipo`, `id_estatus`, `fecha_alta`, `fecha_mod`, `condominio_id`, `usuario_id`, `ubicacion`, `numero_casa`) VALUES (4,'lampara rota','fernandito - Casa 2','la lampara del gimnasio esta rota',1,2,'2026-01-22 20:53:15','2026-01-22 20:54:38',10,29,'en el gimnasio','2'),
(5,'wdAWDWAD','Arlet codero zamora - Casa 1','dwDWAD',1,2,'2026-01-27 22:54:05','2026-01-29 00:14:10',7,21,'wdADWA','1'),
(6,'wdWADWAD','pancho - Casa 4','dwWDAWD',2,1,'2026-01-27 22:54:33',NULL,7,30,'wdwadwa','4'),
(7,'fuga de agua','Arlet codero zamora - Casa 1','cerca de mi baño',1,2,'2026-01-29 00:11:10','2026-01-29 00:14:06',7,21,'entrando a la izquierda torre b','1'),
(9,'Se fue la luz en la caseta','lupita - Casa 1','Los guardias no tienen luz',1,2,'2026-02-04 02:17:44','2026-02-04 02:18:20',13,32,'caseta','1'),
(10,'corrupta','luisito - Casa 2','se nos fue una palabra corrupta en \"ubicación\" dice \"banioo\"',1,2,'2026-02-04 02:18:09','2026-02-04 02:20:35',13,33,'baño','2'),
(11,'basura','lupita - Casa 1','No hicieron limpieza de la basura',2,2,'2026-02-04 02:18:12','2026-02-04 02:20:29',13,32,'calle','1'),
(12,'No limpiaron el asador','fer - Casa 5','Los demás no asean el lugar del asador',1,2,'2026-02-04 02:18:14','2026-02-04 02:20:32',13,35,'','5');
/*!40000 ALTER TABLE `mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `movimientos_pagos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_pagos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `propietario_id` int(11) NOT NULL,
  `tipo` enum('cargo','abono') NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `concepto` varchar(100) NOT NULL,
  `saldo_anterior` decimal(10,2) DEFAULT NULL,
  `saldo_nuevo` decimal(10,2) DEFAULT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `metodo_pago` varchar(50) DEFAULT NULL,
  `fecha_movimiento` timestamp NULL DEFAULT current_timestamp(),
  `usuario_registro` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_registro` (`usuario_registro`),
  KEY `FK3h55nek16xtpqte2nrj621hvs` (`propietario_id`),
  CONSTRAINT `FK3h55nek16xtpqte2nrj621hvs` FOREIGN KEY (`propietario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `movimientos_pagos_ibfk_1` FOREIGN KEY (`propietario_id`) REFERENCES `propietarios` (`id`),
  CONSTRAINT `movimientos_pagos_ibfk_2` FOREIGN KEY (`usuario_registro`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `movimientos_pagos` WRITE;
/*!40000 ALTER TABLE `movimientos_pagos` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `movimientos_pagos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `notificaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificaciones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `prioridad` varchar(255) DEFAULT NULL,
  `leida` tinyint(1) DEFAULT 0,
  `creada_en` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `idx_notificaciones_condominio` (`condominio_id`),
  CONSTRAINT `fk_notificaciones_condominio` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`),
  CONSTRAINT `notificaciones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `notificaciones` WRITE;
/*!40000 ALTER TABLE `notificaciones` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `notificaciones` (`id`, `usuario_id`, `condominio_id`, `titulo`, `descripcion`, `prioridad`, `leida`, `creada_en`) VALUES (27,29,10,'CORTE DE LUZ','SE CORTARA LA LUZ EL 31 DE ENERO POR MANTENIMIENTO','INFORMATIVO',1,'2026-01-22 20:06:06'),
(32,28,10,'ASAMBLEA','ASAMBLE EN FEBRERO','EVENTO',0,'2026-01-22 20:08:17'),
(34,28,10,'FALTA DE AGUA','FALTA DE AGUA','URGENTE',0,'2026-01-22 20:09:50'),
(35,24,10,'X','X','URGENTE',0,'2026-01-22 20:27:21'),
(36,26,10,'X','X','URGENTE',0,'2026-01-22 20:27:21'),
(37,27,10,'X','X','URGENTE',1,'2026-01-22 20:27:21'),
(38,28,10,'X','X','URGENTE',0,'2026-01-22 20:27:21'),
(39,29,10,'X','X','URGENTE',1,'2026-01-22 20:27:21'),
(40,NULL,7,'corte de luz para el sabado','se corta la luz','URGENTE',1,'2026-01-27 21:48:41'),
(43,21,7,'nose','quiza','EVENTO',1,'2026-01-27 22:14:06'),
(44,30,7,'ddadwd','wdwdawd','INFORMATIVO',1,'2026-01-27 22:33:23'),
(45,NULL,7,'san Valentín','se realizara un convivio de 1:00pm a 4:00pm','EVENTO',1,'2026-02-02 21:57:35'),
(46,NULL,13,'Internet sin servicio','sin servicio el dia 12 de febrero','INFORMATIVO',1,'2026-02-04 02:09:40'),
(47,NULL,13,'Falla de Luz','falla de luz','URGENTE',1,'2026-02-04 02:10:22'),
(48,NULL,13,'Candelaria','Te invitamos a los tamales','EVENTO',1,'2026-02-04 02:11:02'),
(49,32,13,'Manetnimiento','ayudanos por favor con el pago','URGENTE',1,'2026-02-04 02:11:40'),
(50,33,13,'Mesa directiva','no olvides la reunion del dia 28 de febrero','EVENTO',1,'2026-02-04 02:12:47'),
(51,NULL,13,'Falta de agua','cortaran el agua el 1ro de marzo tomar precauciones','URGENTE',1,'2026-02-04 02:13:45');
/*!40000 ALTER TABLE `notificaciones` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `pago_apartamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago_apartamentos` (
  `pago_id` int(11) NOT NULL,
  `apartamento_id` int(11) NOT NULL,
  PRIMARY KEY (`pago_id`,`apartamento_id`),
  KEY `apartamento_id` (`apartamento_id`),
  CONSTRAINT `pago_apartamentos_ibfk_1` FOREIGN KEY (`pago_id`) REFERENCES `pagos_programados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `pago_apartamentos_ibfk_2` FOREIGN KEY (`apartamento_id`) REFERENCES `apartamentos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `pago_apartamentos` WRITE;
/*!40000 ALTER TABLE `pago_apartamentos` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `pago_apartamentos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `pago_programado_usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago_programado_usuarios` (
  `pago_programado_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  PRIMARY KEY (`pago_programado_id`,`usuario_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `pago_programado_usuarios_ibfk_1` FOREIGN KEY (`pago_programado_id`) REFERENCES `pagos_programados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `pago_programado_usuarios_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `pago_programado_usuarios` WRITE;
/*!40000 ALTER TABLE `pago_programado_usuarios` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `pago_programado_usuarios` (`pago_programado_id`, `usuario_id`) VALUES (7,21),
(8,21),
(14,21),
(15,21),
(9,26),
(10,26),
(11,26),
(12,26),
(13,26),
(9,27),
(10,27),
(13,27),
(16,32),
(17,32),
(21,32),
(22,32),
(16,33),
(18,33),
(16,35),
(19,35);
/*!40000 ALTER TABLE `pago_programado_usuarios` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `pagos_programados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos_programados` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `concepto` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) DEFAULT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_limite` date DEFAULT NULL,
  `es_recurrente` tinyint(1) DEFAULT 0,
  `periodicidad` int(11) DEFAULT NULL,
  `intervalo_dias` int(11) DEFAULT NULL,
  `repeticiones` int(11) DEFAULT 1,
  `condominio_id` int(11) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `condominio_id` (`condominio_id`),
  CONSTRAINT `pagos_programados_ibfk_1` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `pagos_programados` WRITE;
/*!40000 ALTER TABLE `pagos_programados` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `pagos_programados` (`id`, `concepto`, `descripcion`, `categoria`, `monto`, `fecha_inicio`, `fecha_limite`, `es_recurrente`, `periodicidad`, `intervalo_dias`, `repeticiones`, `condominio_id`, `fecha_creacion`) VALUES (4,'pago','pago seguridad','Seguridad',10000.00,'2026-01-01',NULL,1,30,30,12,7,'2026-01-16 12:13:09'),
(5,'mantenimiento mensual','pafo','Cuota Extraordinaria',10000.00,'2026-02-01',NULL,0,NULL,NULL,1,7,'2026-01-18 01:12:07'),
(7,'mantenimiento mensual','pago','Seguridad',10000.00,'2026-01-01',NULL,0,NULL,NULL,1,7,'2026-01-19 15:38:37'),
(8,'pago','pago','Mantenimiento',11999.98,'2026-01-01',NULL,0,NULL,NULL,1,7,'2026-01-19 19:26:22'),
(9,'Mantenimiento','mantenimiento','Mantenimiento',1250.00,'2026-01-31','2026-02-15',1,30,30,12,10,'2026-01-22 20:00:53'),
(10,'MEDIDOR','medidor de agua','AGUA',120.00,'2026-01-31','2026-02-15',1,15,15,6,10,'2026-01-22 20:02:21'),
(11,'fuga de agua','fuga de agua','AGUA',200.00,'2026-03-01',NULL,0,NULL,NULL,1,10,'2026-01-22 20:03:24'),
(12,'mantenimiento mensual','','Servicios Básicos',10000.00,'2026-01-31',NULL,1,15,15,12,10,'2026-01-26 21:31:37'),
(13,'Mantenimiento','','Servicios Básicos',1000.00,'2026-01-31',NULL,0,NULL,NULL,1,10,'2026-01-26 22:05:22'),
(14,'Mantenimiento','','Administración',2499.99,'2026-01-31','2026-03-02',1,30,30,12,7,'2026-01-26 22:07:07'),
(15,'mantenimiento','','Mantenimiento',1000.00,'2026-02-27','2026-03-14',1,15,15,12,7,'2026-02-02 21:39:15'),
(16,'Mantenimiento','pago manetenimiento','Mantenimiento',1000.00,'2026-02-01','2026-03-03',1,30,30,12,13,'2026-02-04 01:54:51'),
(17,'Medidor','medidor','Servicios Básicos',500.00,'2026-03-01','2026-03-31',1,60,60,6,13,'2026-02-04 01:56:36'),
(18,'Medidor','medidor','Servicios Básicos',500.00,'2026-03-01','2026-03-31',0,NULL,NULL,1,13,'2026-02-04 01:57:26'),
(19,'Medidor','medidor','Mantenimiento',5000.00,'2026-03-01','2026-03-31',1,180,180,2,13,'2026-02-04 01:58:17'),
(21,'Internet','','Cuota Extraordinaria',1000.00,'2026-06-01','2026-06-16',0,NULL,NULL,1,13,'2026-02-04 02:05:23'),
(22,'Internet','internet','Cuota Extraordinaria',1500.00,'2026-07-01','2026-07-16',0,NULL,NULL,1,13,'2026-02-04 02:06:38');
/*!40000 ALTER TABLE `pagos_programados` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `pagos_stripe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos_stripe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `propietario_id` int(11) DEFAULT NULL,
  `saldos_id` int(11) DEFAULT NULL,
  `stripe_payment_id` varchar(255) NOT NULL,
  `stripe_customer_id` varchar(255) DEFAULT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `moneda` varchar(255) DEFAULT NULL,
  `concepto` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL,
  `ultimos_4_digitos` varchar(255) DEFAULT NULL,
  `marca_tarjeta` varchar(255) DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT current_timestamp(),
  `fecha_confirmacion` datetime DEFAULT NULL,
  `fecha_expiracion` datetime DEFAULT NULL,
  `error_code` varchar(255) DEFAULT NULL,
  `error_mensaje` varchar(255) DEFAULT NULL,
  `saldo_anterior` decimal(38,2) DEFAULT NULL,
  `saldo_nuevo` decimal(38,2) DEFAULT NULL,
  `es_parcial` tinyint(1) DEFAULT 0,
  `comprobante_url` varchar(255) DEFAULT NULL,
  `referencia_factura` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `stripe_payment_id` (`stripe_payment_id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `propietario_id` (`propietario_id`),
  KEY `fk_pagos_saldos` (`saldos_id`),
  CONSTRAINT `fk_pagos_saldos` FOREIGN KEY (`saldos_id`) REFERENCES `saldos` (`id`),
  CONSTRAINT `pagos_stripe_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `pagos_stripe_ibfk_2` FOREIGN KEY (`propietario_id`) REFERENCES `propietarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `pagos_stripe` WRITE;
/*!40000 ALTER TABLE `pagos_stripe` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `pagos_stripe` (`id`, `usuario_id`, `propietario_id`, `saldos_id`, `stripe_payment_id`, `stripe_customer_id`, `monto`, `moneda`, `concepto`, `estado`, `metodo_pago`, `ultimos_4_digitos`, `marca_tarjeta`, `fecha_creacion`, `fecha_confirmacion`, `fecha_expiracion`, `error_code`, `error_mensaje`, `saldo_anterior`, `saldo_nuevo`, `es_parcial`, `comprobante_url`, `referencia_factura`) VALUES (1,21,NULL,NULL,'pi_3SrQx1EQUSVpykCn1xvYORGJ',NULL,10000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-19 16:29:36','2026-01-19 16:29:36',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(2,21,NULL,NULL,'pi_3SrTtREQUSVpykCn14h4no6C',NULL,11999.98,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-19 19:38:05','2026-01-19 19:38:05',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(3,27,NULL,NULL,'pi_3SsZzVEQUSVpykCn0bPuzgdo',NULL,1250.00,'MXN','Pago: Mantenimiento - 1/12','succeeded',NULL,NULL,NULL,'2026-01-22 20:20:55','2026-01-22 20:20:55',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(4,27,NULL,NULL,'pi_3SsZzsEQUSVpykCn1NOhzsbf',NULL,120.00,'MXN','Pago: MEDIDOR - 1/6','succeeded',NULL,NULL,NULL,'2026-01-22 20:21:17','2026-01-22 20:21:17',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(5,26,NULL,NULL,'pi_3SsacHEQUSVpykCn0PSI4fiX',NULL,15920.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-22 21:00:57','2026-01-22 21:00:57',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(6,27,NULL,NULL,'pi_3Su2vmEQUSVpykCn0dE4sqZN',NULL,14350.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-26 21:27:05','2026-01-26 21:27:05',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(7,26,NULL,NULL,'pi_3Su30oEQUSVpykCn0L20Jt3i',NULL,10000.00,'MXN','Pago: mantenimiento mensual - 1/12','succeeded',NULL,NULL,NULL,'2026-01-26 21:32:18','2026-01-26 21:32:18',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(8,26,NULL,NULL,'pi_3Su31WEQUSVpykCn0PjaNZ2D',NULL,110000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-26 21:33:02','2026-01-26 21:33:02',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(9,27,NULL,NULL,'pi_3Su3aFEQUSVpykCn1KlA1rO3',NULL,1000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-26 22:08:55','2026-01-26 22:08:55',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(10,21,NULL,NULL,'pi_3Su3b9EQUSVpykCn0yEZ4Tss',NULL,29999.88,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-26 22:09:50','2026-01-26 22:09:50',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL),
(11,26,NULL,NULL,'pi_3Su3dyEQUSVpykCn0xkCCk1a',NULL,1000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-01-26 22:12:45','2026-01-26 22:12:45',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(12,32,NULL,NULL,'pi_3SwvPZEQUSVpykCn0mKRnRWl',NULL,1000.00,'MXN','Pago: Mantenimiento - 1/12','succeeded',NULL,NULL,NULL,'2026-02-04 02:01:47','2026-02-04 02:01:47',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(13,35,NULL,NULL,'pi_3SwvQ5EQUSVpykCn0LqbPDfk',NULL,1000.00,'MXN','Pago: Mantenimiento - 1/12','succeeded',NULL,NULL,NULL,'2026-02-04 02:02:19','2026-02-04 02:02:19',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(14,33,NULL,NULL,'pi_3SwvQ6EQUSVpykCn0DAlNS8l',NULL,500.00,'MXN','Pago: Medidor','succeeded',NULL,NULL,NULL,'2026-02-04 02:02:19','2026-02-04 02:02:19',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(15,32,NULL,NULL,'pi_3SwvQEEQUSVpykCn0SqdeevF',NULL,14000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-02-04 02:02:27','2026-02-04 02:02:27',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(16,33,NULL,NULL,'pi_3SwvQqEQUSVpykCn1kBMJJT2',NULL,12000.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-02-04 02:03:06','2026-02-04 02:03:06',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL),
(17,32,NULL,NULL,'pi_3SwvymEQUSVpykCn1iPQoLPB',NULL,2500.00,'MXN','Pago total pendiente','succeeded',NULL,NULL,NULL,'2026-02-04 02:38:10','2026-02-04 02:38:10',NULL,NULL,NULL,NULL,NULL,0,NULL,NULL);
/*!40000 ALTER TABLE `pagos_stripe` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `propietarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `propietarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `tipo_propiedad` enum('propietario','copropietario','inquilino') DEFAULT NULL,
  `desde` date DEFAULT NULL,
  `hasta` date DEFAULT NULL,
  `creado_en` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `condominio_id` (`condominio_id`),
  CONSTRAINT `propietarios_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `propietarios_ibfk_2` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `propietarios` WRITE;
/*!40000 ALTER TABLE `propietarios` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `propietarios` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `reportes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reportes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `creado_en` datetime DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKold004glimsqiwcj212jmyx22` (`condominio_id`),
  KEY `FKjb16uevkap6vb4wd2kimdomuc` (`usuario_id`),
  CONSTRAINT `FKjb16uevkap6vb4wd2kimdomuc` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKold004glimsqiwcj212jmyx22` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `reportes` WRITE;
/*!40000 ALTER TABLE `reportes` DISABLE KEYS */;
set autocommit=0;
/*!40000 ALTER TABLE `reportes` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `reservas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) DEFAULT NULL,
  `amenidad` varchar(255) DEFAULT NULL,
  `fecha_reserva` date DEFAULT NULL,
  `hora_reserva` time DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `creada_en` timestamp NULL DEFAULT current_timestamp(),
  `dia` int(11) DEFAULT NULL,
  `mes` int(11) DEFAULT NULL,
  `anio` int(11) DEFAULT NULL,
  `hora_inicio` time DEFAULT NULL,
  `hora_fin` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `reservas` WRITE;
/*!40000 ALTER TABLE `reservas` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `reservas` (`id`, `usuario_id`, `amenidad`, `fecha_reserva`, `hora_reserva`, `estado`, `creada_en`, `dia`, `mes`, `anio`, `hora_inicio`, `hora_fin`) VALUES (1,21,'SPA','2026-01-19','20:00:00','confirmada',NULL,19,1,2026,'20:00:00','21:00:00'),
(2,21,'sala de juegos','2026-01-20','17:00:00','confirmada',NULL,20,1,2026,'17:00:00','18:00:00'),
(3,21,'SPA','2026-02-02','18:00:00','confirmada',NULL,2,2,2026,'18:00:00','19:00:00'),
(4,21,'SPA','2026-01-21','10:00:00','confirmada',NULL,21,1,2026,'10:00:00','11:00:00'),
(5,21,'SPA','2026-01-22','10:00:00','confirmada',NULL,22,1,2026,'10:00:00','11:00:00'),
(6,21,'SPA','2026-01-23','17:00:00','confirmada',NULL,23,1,2026,'17:00:00','18:00:00'),
(7,21,'SPA','2026-01-26','14:00:00','confirmada',NULL,26,1,2026,'14:00:00','15:00:00'),
(8,27,'ALBERCA','2026-01-22','19:00:00','confirmada',NULL,22,1,2026,'19:00:00','20:00:00'),
(9,27,'GIMNASIO','2026-01-31','20:00:00','confirmada',NULL,31,1,2026,'20:00:00','21:00:00'),
(10,33,'PADEL','2026-02-13','10:00:00','confirmada',NULL,13,2,2026,'10:00:00','11:00:00'),
(11,32,'ASADOR','2026-02-07','13:00:00','confirmada',NULL,7,2,2026,'13:00:00','14:00:00'),
(12,32,'ASADOR','2026-02-08','14:00:00','confirmada',NULL,8,2,2026,'14:00:00','15:00:00'),
(13,33,'ASADOR','2026-02-07','15:00:00','confirmada',NULL,7,2,2026,'15:00:00','16:00:00'),
(14,35,'ASADOR','2026-02-03','13:00:00','confirmada',NULL,3,2,2026,'13:00:00','14:00:00'),
(15,33,'ASADOR','2026-02-08','15:00:00','confirmada',NULL,8,2,2026,'15:00:00','16:00:00'),
(16,35,'ASADOR','2026-02-08','13:00:00','confirmada',NULL,8,2,2026,'13:00:00','14:00:00'),
(17,32,'ASADOR','2026-02-21','14:00:00','confirmada',NULL,21,2,2026,'14:00:00','15:00:00'),
(18,33,'ASADOR','2026-02-05','10:00:00','confirmada',NULL,5,2,2026,'10:00:00','11:00:00'),
(19,35,'ALBERCA','2026-02-14','13:00:00','confirmada',NULL,14,2,2026,'13:00:00','14:00:00'),
(20,33,'ASADOR','2026-02-14','13:00:00','confirmada',NULL,14,2,2026,'13:00:00','14:00:00'),
(21,32,'ASADOR','2026-02-14','14:00:00','confirmada',NULL,14,2,2026,'14:00:00','15:00:00'),
(22,32,'PADEL','2026-02-28','15:00:00','confirmada',NULL,28,2,2026,'15:00:00','16:00:00'),
(23,35,'PADEL','2026-02-28','14:00:00','confirmada',NULL,28,2,2026,'14:00:00','15:00:00'),
(24,33,'PADEL','2026-02-28','16:00:00','confirmada',NULL,28,2,2026,'16:00:00','17:00:00'),
(25,32,'PADEL','2026-02-03','11:00:00','confirmada',NULL,3,2,2026,'11:00:00','12:00:00'),
(26,32,'ALBERCA','2026-02-11','11:00:00','confirmada',NULL,11,2,2026,'11:00:00','12:00:00');
/*!40000 ALTER TABLE `reservas` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `roles` (`id`, `nombre`) VALUES (1,'admin_usuario'),
(4,'COPO'),
(3,'SEGURIDAD'),
(2,'USUARIO');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `saldos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `saldos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `propietario_id` int(11) DEFAULT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `monto` decimal(38,2) DEFAULT NULL,
  `concepto` varchar(255) DEFAULT NULL,
  `fecha_limite` date DEFAULT NULL,
  `mes` int(11) DEFAULT NULL,
  `año` int(11) DEFAULT NULL,
  `pagado` tinyint(1) DEFAULT 0,
  `actualizado_en` timestamp NULL DEFAULT current_timestamp(),
  `tipo_pago` varchar(255) DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL,
  `referencia` varchar(255) DEFAULT NULL,
  `fecha_pago` date DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `saldo_actual` decimal(38,2) DEFAULT NULL,
  `ultimo_movimiento` timestamp NULL DEFAULT NULL,
  `referencia_factura` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) DEFAULT NULL,
  `es_recurrente` tinyint(1) DEFAULT 0,
  `numero_repeticion` int(11) DEFAULT 1,
  `pago_programado_id` int(11) DEFAULT NULL,
  `fecha_pago_completado` datetime DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `fk_saldo_propietario` (`propietario_id`),
  KEY `fk_saldos_condominio` (`condominio_id`),
  CONSTRAINT `fk_saldo_propietario` FOREIGN KEY (`propietario_id`) REFERENCES `propietarios` (`id`),
  CONSTRAINT `fk_saldos_condominio` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`),
  CONSTRAINT `saldos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `saldos` WRITE;
/*!40000 ALTER TABLE `saldos` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `saldos` (`id`, `propietario_id`, `usuario_id`, `monto`, `concepto`, `fecha_limite`, `mes`, `año`, `pagado`, `actualizado_en`, `tipo_pago`, `metodo_pago`, `referencia`, `fecha_pago`, `descripcion`, `saldo_actual`, `ultimo_movimiento`, `referencia_factura`, `categoria`, `es_recurrente`, `numero_repeticion`, `pago_programado_id`, `fecha_pago_completado`, `condominio_id`) VALUES (13,NULL,12,1200.00,'mantenimiento mensual - 1/12','2026-01-08',1,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-01-01','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,1,3,NULL,NULL),
(14,NULL,12,1200.00,'mantenimiento mensual - 2/12','2026-02-07',2,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-01-31','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,2,3,NULL,NULL),
(15,NULL,12,1200.00,'mantenimiento mensual - 3/12','2026-03-09',3,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-03-02','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,3,3,NULL,NULL),
(16,NULL,12,1200.00,'mantenimiento mensual - 4/12','2026-04-08',4,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-04-01','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,4,3,NULL,NULL),
(17,NULL,12,1200.00,'mantenimiento mensual - 5/12','2026-05-08',5,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-05-01','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,5,3,NULL,NULL),
(18,NULL,12,1200.00,'mantenimiento mensual - 6/12','2026-06-07',6,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-05-31','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,6,3,NULL,NULL),
(19,NULL,12,1200.00,'mantenimiento mensual - 7/12','2026-07-07',7,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-06-30','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,7,3,NULL,NULL),
(20,NULL,12,1200.00,'mantenimiento mensual - 8/12','2026-08-06',8,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-07-30','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,8,3,NULL,NULL),
(21,NULL,12,1200.00,'mantenimiento mensual - 9/12','2026-09-05',9,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-08-29','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,9,3,NULL,NULL),
(22,NULL,12,1200.00,'mantenimiento mensual - 10/12','2026-10-05',10,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-09-28','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,10,3,NULL,NULL),
(23,NULL,12,1200.00,'mantenimiento mensual - 11/12','2026-11-04',11,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-10-28','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,11,3,NULL,NULL),
(24,NULL,12,1200.00,'mantenimiento mensual - 12/12','2026-12-04',12,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-11-27','pago',1200.00,'2026-01-14 18:13:35',NULL,'Servicios Básicos',1,12,3,NULL,NULL),
(74,NULL,21,10000.00,'mantenimiento mensual',NULL,NULL,NULL,1,NULL,'PROGRAMADO',NULL,NULL,'2026-01-19','pago',0.00,'2026-01-19 16:29:36',NULL,'Seguridad',0,1,7,'2026-01-19 16:29:36',7),
(76,NULL,21,11999.98,'pago',NULL,NULL,NULL,1,NULL,'PROGRAMADO',NULL,NULL,'2026-01-19','pago',0.00,'2026-01-19 19:38:05',NULL,'Mantenimiento',0,1,8,'2026-01-19 19:38:05',7),
(78,NULL,12,1200.00,'Mantenimiento','2025-07-23',7,2025,1,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(79,NULL,13,1200.00,'Mantenimiento','2025-07-23',7,2025,1,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(80,NULL,12,1500.00,'Mantenimiento','2025-07-23',7,2025,1,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(81,NULL,NULL,3500.00,'Limpieza áreas comunes','2025-07-23',7,2025,0,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,7),
(82,NULL,NULL,2800.00,'Agua','2025-07-23',7,2025,0,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,7),
(83,NULL,NULL,5000.00,'Seguridad','2025-07-23',7,2025,0,'2026-01-23 01:32:49',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,8),
(84,NULL,12,1200.00,'Mantenimiento','2025-08-23',8,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(85,NULL,21,1200.00,'Mantenimiento','2025-08-23',8,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(86,NULL,23,1500.00,'Mantenimiento','2025-08-23',8,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(87,NULL,NULL,3200.00,'Luz','2025-08-23',8,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,7),
(88,NULL,NULL,7500.00,'Mantenimiento ascensor','2025-08-23',8,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Reparaciones',0,1,NULL,NULL,8),
(89,NULL,12,1200.00,'Mantenimiento','2025-09-23',9,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(90,NULL,13,1200.00,'Mantenimiento','2025-09-23',9,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(91,NULL,21,1200.00,'Mantenimiento','2025-09-23',9,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(92,NULL,23,1500.00,'Mantenimiento','2025-09-23',9,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(93,NULL,NULL,1800.00,'Gas','2025-09-23',9,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,7),
(94,NULL,NULL,4200.00,'Jardinería','2025-09-23',9,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Mantenimiento',0,1,NULL,NULL,8),
(95,NULL,12,1200.00,'Mantenimiento','2025-10-23',10,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(96,NULL,13,1200.00,'Mantenimiento','2025-10-23',10,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(97,NULL,23,1500.00,'Mantenimiento','2025-10-23',10,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(98,NULL,NULL,6500.00,'Reparación portón','2025-10-23',10,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Reparaciones',0,1,NULL,NULL,7),
(99,NULL,NULL,3800.00,'Limpieza cisterna','2025-10-23',10,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Mantenimiento',0,1,NULL,NULL,8),
(100,NULL,12,1200.00,'Mantenimiento','2025-11-23',11,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(101,NULL,21,1200.00,'Mantenimiento','2025-11-23',11,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(102,NULL,23,1500.00,'Mantenimiento','2025-11-23',11,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(103,NULL,NULL,12500.00,'Pintura fachada','2025-11-23',11,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Mejoras',0,1,NULL,NULL,7),
(104,NULL,NULL,850.00,'Internet áreas comunes','2025-11-23',11,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Servicios',0,1,NULL,NULL,8),
(105,NULL,12,1200.00,'Mantenimiento','2025-12-23',12,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(106,NULL,13,1200.00,'Mantenimiento','2025-12-23',12,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(107,NULL,21,1200.00,'Mantenimiento','2025-12-23',12,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,7),
(108,NULL,23,1500.00,'Mantenimiento','2025-12-23',12,2025,1,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Cuotas',0,1,NULL,NULL,8),
(109,NULL,NULL,3000.00,'Salario administrador','2025-12-23',12,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Personal',0,1,NULL,NULL,7),
(110,NULL,NULL,5600.00,'Mantenimiento alberca','2025-12-23',12,2025,0,'2026-01-23 01:32:50',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Mantenimiento',0,1,NULL,NULL,8),
(111,NULL,26,1250.00,'Mantenimiento - 1/12','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,1,9,'2026-01-22 21:00:57',10),
(112,NULL,26,1250.00,'Mantenimiento - 2/12','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,2,9,'2026-01-22 21:00:57',10),
(113,NULL,26,1250.00,'Mantenimiento - 3/12','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,3,9,'2026-01-22 21:00:57',10),
(114,NULL,26,1250.00,'Mantenimiento - 4/12','2026-05-08',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,4,9,'2026-01-22 21:00:57',10),
(115,NULL,26,1250.00,'Mantenimiento - 5/12','2026-06-07',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,5,9,'2026-01-22 21:00:57',10),
(116,NULL,26,1250.00,'Mantenimiento - 6/12','2026-07-07',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,6,9,'2026-01-22 21:00:57',10),
(117,NULL,26,1250.00,'Mantenimiento - 7/12','2026-08-06',8,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,7,9,'2026-01-22 21:00:57',10),
(118,NULL,26,1250.00,'Mantenimiento - 8/12','2026-09-05',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,8,9,'2026-01-22 21:00:57',10),
(119,NULL,26,1250.00,'Mantenimiento - 9/12','2026-10-05',10,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,9,9,'2026-01-22 21:00:57',10),
(120,NULL,26,1250.00,'Mantenimiento - 10/12','2026-11-04',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,10,9,'2026-01-22 21:00:57',10),
(121,NULL,26,1250.00,'Mantenimiento - 11/12','2026-12-04',12,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,11,9,'2026-01-22 21:00:57',10),
(122,NULL,26,1250.00,'Mantenimiento - 12/12','2027-01-03',1,2027,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 21:00:57',NULL,'Mantenimiento',1,12,9,'2026-01-22 21:00:57',10),
(123,NULL,27,1250.00,'Mantenimiento - 1/12','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','mantenimiento',0.00,'2026-01-22 20:20:55',NULL,'Mantenimiento',1,1,9,'2026-01-22 20:20:55',10),
(124,NULL,27,1250.00,'Mantenimiento - 2/12','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,2,9,'2026-01-26 21:27:05',10),
(125,NULL,27,1250.00,'Mantenimiento - 3/12','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,3,9,'2026-01-26 21:27:05',10),
(126,NULL,27,1250.00,'Mantenimiento - 4/12','2026-05-08',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,4,9,'2026-01-26 21:27:05',10),
(127,NULL,27,1250.00,'Mantenimiento - 5/12','2026-06-07',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,5,9,'2026-01-26 21:27:05',10),
(128,NULL,27,1250.00,'Mantenimiento - 6/12','2026-07-07',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,6,9,'2026-01-26 21:27:05',10),
(129,NULL,27,1250.00,'Mantenimiento - 7/12','2026-08-06',8,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,7,9,'2026-01-26 21:27:05',10),
(130,NULL,27,1250.00,'Mantenimiento - 8/12','2026-09-05',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,8,9,'2026-01-26 21:27:05',10),
(131,NULL,27,1250.00,'Mantenimiento - 9/12','2026-10-05',10,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,9,9,'2026-01-26 21:27:05',10),
(132,NULL,27,1250.00,'Mantenimiento - 10/12','2026-11-04',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,10,9,'2026-01-26 21:27:05',10),
(133,NULL,27,1250.00,'Mantenimiento - 11/12','2026-12-04',12,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,11,9,'2026-01-26 21:27:05',10),
(134,NULL,27,1250.00,'Mantenimiento - 12/12','2027-01-03',1,2027,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','mantenimiento',0.00,'2026-01-26 21:27:05',NULL,'Mantenimiento',1,12,9,'2026-01-26 21:27:05',10),
(135,NULL,26,120.00,'MEDIDOR - 1/6','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,1,10,'2026-01-22 21:00:57',10),
(136,NULL,26,120.00,'MEDIDOR - 2/6','2026-02-22',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,2,10,'2026-01-22 21:00:57',10),
(137,NULL,26,120.00,'MEDIDOR - 3/6','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,3,10,'2026-01-22 21:00:57',10),
(138,NULL,26,120.00,'MEDIDOR - 4/6','2026-03-24',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,4,10,'2026-01-22 21:00:57',10),
(139,NULL,26,120.00,'MEDIDOR - 5/6','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,5,10,'2026-01-22 21:00:57',10),
(140,NULL,26,120.00,'MEDIDOR - 6/6','2026-04-23',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',1,6,10,'2026-01-22 21:00:57',10),
(141,NULL,27,120.00,'MEDIDOR - 1/6','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-22','medidor de agua',0.00,'2026-01-22 20:21:17',NULL,'AGUA',1,1,10,'2026-01-22 20:21:17',10),
(142,NULL,27,120.00,'MEDIDOR - 2/6','2026-02-22',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','medidor de agua',0.00,'2026-01-26 21:27:05',NULL,'AGUA',1,2,10,'2026-01-26 21:27:05',10),
(143,NULL,27,120.00,'MEDIDOR - 3/6','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','medidor de agua',0.00,'2026-01-26 21:27:05',NULL,'AGUA',1,3,10,'2026-01-26 21:27:05',10),
(144,NULL,27,120.00,'MEDIDOR - 4/6','2026-03-24',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','medidor de agua',0.00,'2026-01-26 21:27:05',NULL,'AGUA',1,4,10,'2026-01-26 21:27:05',10),
(145,NULL,27,120.00,'MEDIDOR - 5/6','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','medidor de agua',0.00,'2026-01-26 21:27:05',NULL,'AGUA',1,5,10,'2026-01-26 21:27:05',10),
(146,NULL,27,120.00,'MEDIDOR - 6/6','2026-04-23',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','medidor de agua',0.00,'2026-01-26 21:27:05',NULL,'AGUA',1,6,10,'2026-01-26 21:27:05',10),
(147,NULL,26,200.00,'fuga de agua',NULL,NULL,NULL,1,NULL,'PROGRAMADO',NULL,NULL,'2026-01-22','fuga de agua',0.00,'2026-01-22 21:00:57',NULL,'AGUA',0,1,11,'2026-01-22 21:00:57',10),
(148,NULL,26,10000.00,'mantenimiento mensual - 1/12','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:32:18',NULL,'Servicios Básicos',1,1,12,'2026-01-26 21:32:18',10),
(149,NULL,26,10000.00,'mantenimiento mensual - 2/12','2026-02-22',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,2,12,'2026-01-26 21:33:02',10),
(150,NULL,26,10000.00,'mantenimiento mensual - 3/12','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,3,12,'2026-01-26 21:33:02',10),
(151,NULL,26,10000.00,'mantenimiento mensual - 4/12','2026-03-24',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,4,12,'2026-01-26 21:33:02',10),
(152,NULL,26,10000.00,'mantenimiento mensual - 5/12','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,5,12,'2026-01-26 21:33:02',10),
(153,NULL,26,10000.00,'mantenimiento mensual - 6/12','2026-04-23',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,6,12,'2026-01-26 21:33:02',10),
(154,NULL,26,10000.00,'mantenimiento mensual - 7/12','2026-05-08',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,7,12,'2026-01-26 21:33:02',10),
(155,NULL,26,10000.00,'mantenimiento mensual - 8/12','2026-05-23',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,8,12,'2026-01-26 21:33:02',10),
(156,NULL,26,10000.00,'mantenimiento mensual - 9/12','2026-06-07',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,9,12,'2026-01-26 21:33:02',10),
(157,NULL,26,10000.00,'mantenimiento mensual - 10/12','2026-06-22',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,10,12,'2026-01-26 21:33:02',10),
(158,NULL,26,10000.00,'mantenimiento mensual - 11/12','2026-07-07',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,11,12,'2026-01-26 21:33:02',10),
(159,NULL,26,10000.00,'mantenimiento mensual - 12/12','2026-07-22',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 21:33:02',NULL,'Servicios Básicos',1,12,12,'2026-01-26 21:33:02',10),
(160,NULL,26,1000.00,'Mantenimiento',NULL,NULL,NULL,1,NULL,'PROGRAMADO',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:12:45',NULL,'Servicios Básicos',0,1,13,'2026-01-26 22:12:45',10),
(161,NULL,27,1000.00,'Mantenimiento',NULL,NULL,NULL,1,NULL,'PROGRAMADO',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:08:55',NULL,'Servicios Básicos',0,1,13,'2026-01-26 22:08:55',10),
(162,NULL,21,2499.99,'Mantenimiento - 1/12','2026-02-07',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,1,14,'2026-01-26 22:09:50',7),
(163,NULL,21,2499.99,'Mantenimiento - 2/12','2026-03-09',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,2,14,'2026-01-26 22:09:50',7),
(164,NULL,21,2499.99,'Mantenimiento - 3/12','2026-04-08',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,3,14,'2026-01-26 22:09:50',7),
(165,NULL,21,2499.99,'Mantenimiento - 4/12','2026-05-08',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,4,14,'2026-01-26 22:09:50',7),
(166,NULL,21,2499.99,'Mantenimiento - 5/12','2026-06-07',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,5,14,'2026-01-26 22:09:50',7),
(167,NULL,21,2499.99,'Mantenimiento - 6/12','2026-07-07',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,6,14,'2026-01-26 22:09:50',7),
(168,NULL,21,2499.99,'Mantenimiento - 7/12','2026-08-06',8,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,7,14,'2026-01-26 22:09:50',7),
(169,NULL,21,2499.99,'Mantenimiento - 8/12','2026-09-05',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,8,14,'2026-01-26 22:09:50',7),
(170,NULL,21,2499.99,'Mantenimiento - 9/12','2026-10-05',10,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,9,14,'2026-01-26 22:09:50',7),
(171,NULL,21,2499.99,'Mantenimiento - 10/12','2026-11-04',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,10,14,'2026-01-26 22:09:50',7),
(172,NULL,21,2499.99,'Mantenimiento - 11/12','2026-12-04',12,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,11,14,'2026-01-26 22:09:50',7),
(173,NULL,21,2499.99,'Mantenimiento - 12/12','2027-01-03',1,2027,0,NULL,'RECURRENTE',NULL,NULL,'2026-01-26','',0.00,'2026-01-26 22:09:50',NULL,'Administración',1,12,14,NULL,7),
(174,NULL,21,1000.00,'mantenimiento - 1/12','2026-03-06',3,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-02-27','',1000.00,'2026-02-02 21:39:15',NULL,'Mantenimiento',1,1,15,NULL,7),
(175,NULL,21,1000.00,'mantenimiento - 2/12','2026-03-21',3,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-03-14','',1000.00,'2026-02-02 21:39:15',NULL,'Mantenimiento',1,2,15,NULL,7),
(176,NULL,21,1000.00,'mantenimiento - 3/12','2026-04-05',4,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-03-29','',1000.00,'2026-02-02 21:39:15',NULL,'Mantenimiento',1,3,15,NULL,7),
(177,NULL,21,1000.00,'mantenimiento - 4/12','2026-04-20',4,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-04-13','',1000.00,'2026-02-02 21:39:15',NULL,'Mantenimiento',1,4,15,NULL,7),
(178,NULL,21,1000.00,'mantenimiento - 5/12','2026-05-05',5,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-04-28','',1000.00,'2026-02-02 21:39:15',NULL,'Mantenimiento',1,5,15,NULL,7),
(179,NULL,21,1000.00,'mantenimiento - 6/12','2026-05-20',5,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-05-13','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,6,15,NULL,7),
(180,NULL,21,1000.00,'mantenimiento - 7/12','2026-06-04',6,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-05-28','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,7,15,NULL,7),
(181,NULL,21,1000.00,'mantenimiento - 8/12','2026-06-19',6,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-06-12','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,8,15,NULL,7),
(182,NULL,21,1000.00,'mantenimiento - 9/12','2026-07-04',7,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-06-27','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,9,15,NULL,7),
(183,NULL,21,1000.00,'mantenimiento - 10/12','2026-07-19',7,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-07-12','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,10,15,NULL,7),
(184,NULL,21,1000.00,'mantenimiento - 11/12','2026-08-03',8,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-07-27','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,11,15,NULL,7),
(185,NULL,21,1000.00,'mantenimiento - 12/12','2026-08-18',8,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-08-11','',1000.00,'2026-02-02 21:39:16',NULL,'Mantenimiento',1,12,15,NULL,7),
(186,NULL,32,1000.00,'Mantenimiento - 1/12','2026-02-08',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:01:47',NULL,'Mantenimiento',1,1,16,'2026-02-04 02:01:47',13),
(187,NULL,32,1000.00,'Mantenimiento - 2/12','2026-03-10',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,2,16,'2026-02-04 02:02:27',13),
(188,NULL,32,1000.00,'Mantenimiento - 3/12','2026-04-09',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,3,16,'2026-02-04 02:02:27',13),
(189,NULL,32,1000.00,'Mantenimiento - 4/12','2026-05-09',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,4,16,'2026-02-04 02:02:27',13),
(190,NULL,32,1000.00,'Mantenimiento - 5/12','2026-06-08',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,5,16,'2026-02-04 02:02:27',13),
(191,NULL,32,1000.00,'Mantenimiento - 6/12','2026-07-08',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,6,16,'2026-02-04 02:02:27',13),
(192,NULL,32,1000.00,'Mantenimiento - 7/12','2026-08-07',8,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,7,16,'2026-02-04 02:02:27',13),
(193,NULL,32,1000.00,'Mantenimiento - 8/12','2026-09-06',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,8,16,'2026-02-04 02:02:27',13),
(194,NULL,32,1000.00,'Mantenimiento - 9/12','2026-10-06',10,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,9,16,'2026-02-04 02:02:27',13),
(195,NULL,32,1000.00,'Mantenimiento - 10/12','2026-11-05',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,10,16,'2026-02-04 02:02:27',13),
(196,NULL,32,1000.00,'Mantenimiento - 11/12','2026-12-05',12,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,11,16,'2026-02-04 02:02:27',13),
(197,NULL,32,1000.00,'Mantenimiento - 12/12','2027-01-04',1,2027,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:27',NULL,'Mantenimiento',1,12,16,'2026-02-04 02:02:27',13),
(198,NULL,33,1000.00,'Mantenimiento - 1/12','2026-02-08',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,1,16,'2026-02-04 02:03:06',13),
(199,NULL,33,1000.00,'Mantenimiento - 2/12','2026-03-10',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,2,16,'2026-02-04 02:03:06',13),
(200,NULL,33,1000.00,'Mantenimiento - 3/12','2026-04-09',4,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,3,16,'2026-02-04 02:03:06',13),
(201,NULL,33,1000.00,'Mantenimiento - 4/12','2026-05-09',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,4,16,'2026-02-04 02:03:06',13),
(202,NULL,33,1000.00,'Mantenimiento - 5/12','2026-06-08',6,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,5,16,'2026-02-04 02:03:06',13),
(203,NULL,33,1000.00,'Mantenimiento - 6/12','2026-07-08',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,6,16,'2026-02-04 02:03:06',13),
(204,NULL,33,1000.00,'Mantenimiento - 7/12','2026-08-07',8,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,7,16,'2026-02-04 02:03:06',13),
(205,NULL,33,1000.00,'Mantenimiento - 8/12','2026-09-06',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,8,16,'2026-02-04 02:03:06',13),
(206,NULL,33,1000.00,'Mantenimiento - 9/12','2026-10-06',10,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,9,16,'2026-02-04 02:03:06',13),
(207,NULL,33,1000.00,'Mantenimiento - 10/12','2026-11-05',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,10,16,'2026-02-04 02:03:06',13),
(208,NULL,33,1000.00,'Mantenimiento - 11/12','2026-12-05',12,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,11,16,'2026-02-04 02:03:06',13),
(209,NULL,33,1000.00,'Mantenimiento - 12/12','2027-01-04',1,2027,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:03:06',NULL,'Mantenimiento',1,12,16,'2026-02-04 02:03:06',13),
(210,NULL,35,1000.00,'Mantenimiento - 1/12','2026-02-08',2,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','pago manetenimiento',0.00,'2026-02-04 02:02:19',NULL,'Mantenimiento',1,1,16,'2026-02-04 02:02:19',13),
(211,NULL,35,1000.00,'Mantenimiento - 2/12','2026-03-10',3,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-03-03','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,2,16,NULL,13),
(212,NULL,35,1000.00,'Mantenimiento - 3/12','2026-04-09',4,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-04-02','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,3,16,NULL,13),
(213,NULL,35,1000.00,'Mantenimiento - 4/12','2026-05-09',5,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-05-02','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,4,16,NULL,13),
(214,NULL,35,1000.00,'Mantenimiento - 5/12','2026-06-08',6,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-06-01','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,5,16,NULL,13),
(215,NULL,35,1000.00,'Mantenimiento - 6/12','2026-07-08',7,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-07-01','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,6,16,NULL,13),
(216,NULL,35,1000.00,'Mantenimiento - 7/12','2026-08-07',8,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-07-31','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,7,16,NULL,13),
(217,NULL,35,1000.00,'Mantenimiento - 8/12','2026-09-06',9,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-08-30','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,8,16,NULL,13),
(218,NULL,35,1000.00,'Mantenimiento - 9/12','2026-10-06',10,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-09-29','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,9,16,NULL,13),
(219,NULL,35,1000.00,'Mantenimiento - 10/12','2026-11-05',11,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-10-29','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,10,16,NULL,13),
(220,NULL,35,1000.00,'Mantenimiento - 11/12','2026-12-05',12,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-11-28','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,11,16,NULL,13),
(221,NULL,35,1000.00,'Mantenimiento - 12/12','2027-01-04',1,2027,0,NULL,'RECURRENTE',NULL,NULL,'2026-12-28','pago manetenimiento',1000.00,'2026-02-04 01:54:51',NULL,'Mantenimiento',1,12,16,NULL,13),
(222,NULL,32,500.00,'Medidor - 1/6','2026-03-08',3,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,1,17,'2026-02-04 02:02:27',13),
(223,NULL,32,500.00,'Medidor - 2/6','2026-05-07',5,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,2,17,'2026-02-04 02:02:27',13),
(224,NULL,32,500.00,'Medidor - 3/6','2026-07-06',7,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,3,17,'2026-02-04 02:02:27',13),
(225,NULL,32,500.00,'Medidor - 4/6','2026-09-04',9,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,4,17,'2026-02-04 02:02:27',13),
(226,NULL,32,500.00,'Medidor - 5/6','2026-11-03',11,2026,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,5,17,'2026-02-04 02:02:27',13),
(227,NULL,32,500.00,'Medidor - 6/6','2027-01-02',1,2027,1,NULL,'RECURRENTE',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:27',NULL,'Servicios Básicos',1,6,17,'2026-02-04 02:02:27',13),
(228,NULL,33,500.00,'Medidor','2026-03-31',3,2026,1,NULL,'PROGRAMADO',NULL,NULL,'2026-02-04','medidor',0.00,'2026-02-04 02:02:19',NULL,'Servicios Básicos',0,1,18,'2026-02-04 02:02:19',13),
(229,NULL,35,5000.00,'Medidor - 1/2','2026-03-08',3,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-03-01','medidor',5000.00,'2026-02-04 01:58:17',NULL,'Mantenimiento',1,1,19,NULL,13),
(230,NULL,35,5000.00,'Medidor - 2/2','2026-09-04',9,2026,0,NULL,'RECURRENTE',NULL,NULL,'2026-08-28','medidor',5000.00,'2026-02-04 01:58:17',NULL,'Mantenimiento',1,2,19,NULL,13),
(232,NULL,32,1000.00,'Internet','2026-06-16',6,2026,1,NULL,'PROGRAMADO',NULL,NULL,'2026-02-04','',0.00,'2026-02-04 02:38:10',NULL,'Cuota Extraordinaria',0,1,21,'2026-02-04 02:38:10',13),
(233,NULL,32,1500.00,'Internet','2026-07-16',7,2026,1,NULL,'PROGRAMADO',NULL,NULL,'2026-02-04','internet',0.00,'2026-02-04 02:38:10',NULL,'Cuota Extraordinaria',0,1,22,'2026-02-04 02:38:10',13);
/*!40000 ALTER TABLE `saldos` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `tipo_mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_mantenimiento` (
  `id_tipo` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `tipo_mantenimiento` WRITE;
/*!40000 ALTER TABLE `tipo_mantenimiento` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `tipo_mantenimiento` (`id_tipo`, `descripcion`) VALUES (1,'Urgente'),
(2,'Informativo');
/*!40000 ALTER TABLE `tipo_mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `usuario_condominio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_condominio` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  `es_principal` tinyint(1) DEFAULT 0,
  `fecha_asignacion` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `fk_usuario_condominio_condominio` (`condominio_id`),
  CONSTRAINT `fk_usuario_condominio_condominio` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `usuario_condominio_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `usuario_condominio_ibfk_2` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuario_condominio` WRITE;
/*!40000 ALTER TABLE `usuario_condominio` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `usuario_condominio` (`id`, `usuario_id`, `condominio_id`, `es_principal`, `fecha_asignacion`) VALUES (16,14,7,1,'2026-01-19 19:29:10'),
(17,14,8,0,'2026-01-19 19:29:10'),
(18,24,10,1,'2026-01-22 19:46:50'),
(20,25,9,1,'2026-01-22 19:47:52'),
(22,31,13,1,'2026-02-04 01:25:39'),
(23,31,14,0,'2026-02-04 01:25:39');
/*!40000 ALTER TABLE `usuario_condominio` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `correo` varchar(255) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `rol_id` int(11) DEFAULT NULL,
  `creado_en` timestamp NULL DEFAULT current_timestamp(),
  `rol_nombre` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `actualizado_en` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `condominio_id` int(11) DEFAULT NULL,
  `apartamento_id` int(11) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `numero_casa` varchar(255) DEFAULT NULL,
  `stripe_customer_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`),
  KEY `rol_id` (`rol_id`),
  KEY `idx_usuario_apartamento` (`apartamento_id`),
  KEY `idx_usuarios_stripe_customer_id` (`stripe_customer_id`),
  CONSTRAINT `fk_usuario_apartamento` FOREIGN KEY (`apartamento_id`) REFERENCES `apartamentos` (`id`) ON DELETE SET NULL,
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `usuarios` (`id`, `correo`, `contrasena`, `rol_id`, `creado_en`, `rol_nombre`, `nombre`, `telefono`, `actualizado_en`, `condominio_id`, `apartamento_id`, `activo`, `numero_casa`, `stripe_customer_id`) VALUES (8,'kaaj@kaaj.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',4,'2025-12-23 01:50:50','COPO','Administrador COPO','555-0000','2026-01-13 18:16:46',NULL,NULL,1,NULL,NULL),
(12,'omar@gmail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-13 17:08:37','USUARIO','omar','55555555555','2026-01-13 17:08:37',1,NULL,1,'1',NULL),
(13,'pancho@gmail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-13 17:13:18','USUARIO','pancho','2111111111','2026-01-13 17:13:18',1,NULL,1,'2',NULL),
(14,'paolina@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2026-01-13 18:17:24','admin_usuario','paolina  cruz','11111111111111','2026-01-26 22:05:42',7,NULL,1,NULL,NULL),
(21,'arlet@gmail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-19 11:28:01','USUARIO','Arlet codero zamora','5524164533','2026-01-26 22:09:49',7,NULL,1,'1',NULL),
(23,'roberto@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-19 19:29:54','USUARIO','roberto','55555555555','2026-01-19 19:29:54',8,NULL,1,'1',NULL),
(24,'jose@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2026-01-22 19:46:50','admin_usuario','JOSE','87654321','2026-01-22 19:46:50',10,NULL,1,NULL,NULL),
(25,'ricardo@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2026-01-22 19:47:52','admin_usuario','ricardo','55555555','2026-01-22 19:47:52',9,NULL,1,NULL,NULL),
(26,'pepito@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-22 19:50:21','USUARIO','PEPITO','12345678','2026-01-26 22:12:44',10,NULL,1,'1',NULL),
(27,'lupita@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-22 19:50:52','USUARIO','lupita','1234543','2026-01-26 22:08:18',10,NULL,1,'2',NULL),
(28,'chuchito@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',3,'2026-01-22 19:51:47','SEGURIDAD','chuchito','123456789','2026-01-22 19:51:47',10,NULL,1,'3',NULL),
(29,'fernandito@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-22 19:52:49','USUARIO','fernandito','98765441','2026-01-22 19:52:56',10,NULL,0,'2',NULL),
(30,'pancho@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-01-27 22:33:03','USUARIO','pancho','23123123123','2026-01-27 22:33:03',7,NULL,1,'4',NULL),
(31,'roberto@kaaj.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2026-02-04 01:18:33','admin_usuario','ROBERTO','562414151','2026-02-04 01:25:39',13,NULL,1,NULL,NULL),
(32,'lupita2@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-02-04 01:20:28','USUARIO','lupita','4324435435','2026-02-04 02:01:45',13,NULL,1,'1',NULL),
(33,'luisito@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-02-04 01:21:01','USUARIO','luisito','224314242443','2026-02-04 02:01:16',13,NULL,1,'2',NULL),
(35,'fer@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',2,'2026-02-04 01:42:58','USUARIO','fer','4343243442','2026-02-04 01:59:08',13,NULL,1,'5',NULL),
(36,'paco@mail.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',3,'2026-02-04 02:28:38','SEGURIDAD','paco','34213213123','2026-02-04 02:28:38',13,NULL,1,'1',NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `visitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `visitas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_qr` varchar(255) NOT NULL,
  `nombre_visitante` varchar(255) NOT NULL,
  `apellido_visitado` varchar(255) NOT NULL,
  `condominio` varchar(255) NOT NULL,
  `tipo_acceso` enum('Visitante','Contratista','Entrega','Personal') NOT NULL,
  `fecha_programada` date NOT NULL,
  `hora_programada` time NOT NULL,
  `estado` enum('Generado','Utilizado','Expirado') DEFAULT 'Generado',
  `fecha_utilizacion` datetime DEFAULT NULL,
  `creado_en` timestamp NULL DEFAULT current_timestamp(),
  `usuario_id` int(11) DEFAULT NULL,
  `condominio_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo_qr` (`codigo_qr`),
  KEY `usuario_id` (`usuario_id`),
  KEY `fk_visita_condominio` (`condominio_id`),
  CONSTRAINT `fk_visita_condominio` FOREIGN KEY (`condominio_id`) REFERENCES `condominios` (`id`),
  CONSTRAINT `visitas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `visitas` WRITE;
/*!40000 ALTER TABLE `visitas` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `visitas` (`id`, `codigo_qr`, `nombre_visitante`, `apellido_visitado`, `condominio`, `tipo_acceso`, `fecha_programada`, `hora_programada`, `estado`, `fecha_utilizacion`, `creado_en`, `usuario_id`, `condominio_id`) VALUES (1,'KAJ-VIS-95387B17','Guillermo','Cordero','A21','Visitante','2026-01-25','20:26:00','Generado',NULL,'2026-01-24 20:36:31',NULL,NULL),
(2,'KAJ-VIS-3E56F42D','arlet','paco','1','Visitante','2026-01-31','10:30:00','Generado',NULL,'2026-01-24 21:29:53',NULL,NULL),
(3,'KAJ-VIS-A6EB1D76','arlet','paco','1','Visitante','2026-01-24','22:00:00','Generado',NULL,'2026-01-24 21:38:35',NULL,NULL),
(4,'KAJ-VIS-71A85ADF','arlet','paco','1','Visitante','2026-01-25','22:00:00','Generado',NULL,'2026-01-24 21:45:13',NULL,NULL),
(5,'KAJ-VIS-F164BE30','arlet','paco','1','Visitante','2026-02-04','09:00:00','Generado',NULL,'2026-02-03 04:53:41',NULL,NULL),
(6,'KAJ-VIS-F9587D2B','francisco','arlet','401','Visitante','2026-02-04','23:05:00','Generado',NULL,'2026-02-03 05:06:50',NULL,NULL),
(7,'KAJ-VIS-4CFE35AC','Guillermo','awdwad','dwdaw','Contratista','2026-02-03','23:06:00','Generado',NULL,'2026-02-03 05:07:03',NULL,NULL),
(8,'KAJ-VIS-A891F71B','Pedro','Lupita','Petrel','Visitante','2026-02-04','20:27:00','Generado',NULL,'2026-02-04 02:27:25',NULL,NULL);
/*!40000 ALTER TABLE `visitas` ENABLE KEYS */;
UNLOCK TABLES;
commit;
DROP TABLE IF EXISTS `vista_finanzas_completa`;
/*!50001 DROP VIEW IF EXISTS `vista_finanzas_completa`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `vista_finanzas_completa` AS SELECT
 1 AS `id_unico`,
  1 AS `tipo`,
  1 AS `concepto`,
  1 AS `descripcion`,
  1 AS `monto`,
  1 AS `fecha`,
  1 AS `mes`,
  1 AS `año`,
  1 AS `condominio_id`,
  1 AS `usuario_id`,
  1 AS `estatus`,
  1 AS `color`,
  1 AS `categoria_nombre`,
  1 AS `created_at` */;
SET character_set_client = @saved_cs_client;
/*!50001 DROP VIEW IF EXISTS `vista_finanzas_completa`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_uca1400_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vista_finanzas_completa` AS select concat('I',`i`.`id`) AS `id_unico`,'INGRESO' AS `tipo`,`i`.`concepto` AS `concepto`,`i`.`descripcion` AS `descripcion`,`i`.`monto` AS `monto`,`i`.`fecha` AS `fecha`,`i`.`mes` AS `mes`,`i`.`año` AS `año`,`i`.`condominio_id` AS `condominio_id`,`i`.`usuario_id` AS `usuario_id`,`i`.`estatus` AS `estatus`,`ci`.`color` AS `color`,`ci`.`nombre` AS `categoria_nombre`,`i`.`created_at` AS `created_at` from (`ingresos` `i` left join `categorias_ingresos` `ci` on(`i`.`categoria_id` = `ci`.`id`)) union all select concat('E',`e`.`id`) AS `id_unico`,'EGRESO' AS `tipo`,`e`.`concepto` AS `concepto`,`e`.`descripcion` AS `descripcion`,`e`.`monto` AS `monto`,`e`.`fecha` AS `fecha`,`e`.`mes` AS `mes`,`e`.`año` AS `año`,`e`.`condominio_id` AS `condominio_id`,`e`.`usuario_id` AS `usuario_id`,`e`.`estatus` AS `estatus`,`ce`.`color` AS `color`,`ce`.`nombre` AS `categoria_nombre`,`e`.`created_at` AS `created_at` from (`egresos` `e` left join `categorias_egresos` `ce` on(`e`.`categoria_id` = `ce`.`id`)) union all select concat('S',`s`.`id`) AS `id_unico`,case when `s`.`pagado` = 1 then 'INGRESO' else 'EGRESO' end AS `tipo`,`s`.`concepto` AS `concepto`,`s`.`descripcion` AS `descripcion`,`s`.`monto` AS `monto`,`s`.`fecha_limite` AS `fecha`,month(`s`.`fecha_limite`) AS `mes`,year(`s`.`fecha_limite`) AS `año`,`s`.`condominio_id` AS `condominio_id`,`s`.`usuario_id` AS `usuario_id`,case when `s`.`pagado` = 1 then 'pagado' else 'pendiente' end AS `estatus`,'#6B7280' AS `color`,`s`.`categoria` AS `categoria_nombre`,`s`.`actualizado_en` AS `created_at` from `saldos` `s` where `s`.`monto` > 0 */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

