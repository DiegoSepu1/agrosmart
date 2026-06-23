-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: agrosmart_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `agricultor`
--

DROP TABLE IF EXISTS `agricultor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agricultor` (
  `id_agricultor` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `rut` varchar(12) NOT NULL,
  `razon_social` varchar(150) DEFAULT NULL,
  `direccion_predio` text DEFAULT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(11,8) DEFAULT NULL,
  `tiene_linea_credito` tinyint(1) DEFAULT 0,
  `monto_credito_maximo` decimal(12,2) DEFAULT 0.00,
  `saldo_credito_disponible` decimal(12,2) DEFAULT 0.00,
  PRIMARY KEY (`id_agricultor`),
  UNIQUE KEY `rut` (`rut`),
  KEY `fk_agricultor_usuario` (`id_usuario`),
  CONSTRAINT `fk_agricultor_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agricultor`
--

LOCK TABLES `agricultor` WRITE;
/*!40000 ALTER TABLE `agricultor` DISABLE KEYS */;
/*!40000 ALTER TABLE `agricultor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aprobacion_credito`
--

DROP TABLE IF EXISTS `aprobacion_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aprobacion_credito` (
  `id_aprobacion` int(11) NOT NULL AUTO_INCREMENT,
  `id_orden` int(11) NOT NULL,
  `id_asesor` int(11) NOT NULL,
  `id_encargado_cobranzas` int(11) NOT NULL,
  `fecha_aprobacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `monto_ocupado_credito` decimal(12,2) NOT NULL,
  `comentarios` text DEFAULT NULL,
  PRIMARY KEY (`id_aprobacion`),
  KEY `fk_aprobacion_orden` (`id_orden`),
  KEY `fk_aprobacion_asesor` (`id_asesor`),
  KEY `fk_aprobacion_cobranzas` (`id_encargado_cobranzas`),
  CONSTRAINT `fk_aprobacion_asesor` FOREIGN KEY (`id_asesor`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_aprobacion_cobranzas` FOREIGN KEY (`id_encargado_cobranzas`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_aprobacion_orden` FOREIGN KEY (`id_orden`) REFERENCES `orden` (`id_orden`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aprobacion_credito`
--

LOCK TABLES `aprobacion_credito` WRITE;
/*!40000 ALTER TABLE `aprobacion_credito` DISABLE KEYS */;
/*!40000 ALTER TABLE `aprobacion_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_orden`
--

DROP TABLE IF EXISTS `detalle_orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_orden` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_orden` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario_clp` decimal(12,2) NOT NULL,
  `precio_unitario_usd` decimal(10,2) NOT NULL,
  `id_sucursal_origen` int(11) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `fk_detalle_orden` (`id_orden`),
  KEY `fk_detalle_producto` (`id_producto`),
  KEY `fk_detalle_sucursal` (`id_sucursal_origen`),
  CONSTRAINT `fk_detalle_orden` FOREIGN KEY (`id_orden`) REFERENCES `orden` (`id_orden`) ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `fk_detalle_sucursal` FOREIGN KEY (`id_sucursal_origen`) REFERENCES `sucursal` (`id_sucursal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_orden`
--

LOCK TABLES `detalle_orden` WRITE;
/*!40000 ALTER TABLE `detalle_orden` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_orden` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribuidor_rural`
--

DROP TABLE IF EXISTS `distribuidor_rural`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distribuidor_rural` (
  `id_distribuidor` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `rut` varchar(12) NOT NULL,
  `api_key` varchar(255) NOT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `limite_consultas_diarias` int(11) DEFAULT 100,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_distribuidor`),
  UNIQUE KEY `api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidor_rural`
--

LOCK TABLES `distribuidor_rural` WRITE;
/*!40000 ALTER TABLE `distribuidor_rural` DISABLE KEYS */;
/*!40000 ALTER TABLE `distribuidor_rural` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ficha_tecnica_detallada`
--

DROP TABLE IF EXISTS `ficha_tecnica_detallada`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ficha_tecnica_detallada` (
  `id_ficha` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) NOT NULL,
  `especificaciones_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`especificaciones_json`)),
  `manual_pdf_url` varchar(255) DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_ficha`),
  KEY `fk_ficha_producto` (`id_producto`),
  CONSTRAINT `fk_ficha_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ficha_tecnica_detallada`
--

LOCK TABLES `ficha_tecnica_detallada` WRITE;
/*!40000 ALTER TABLE `ficha_tecnica_detallada` DISABLE KEYS */;
/*!40000 ALTER TABLE `ficha_tecnica_detallada` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `id_inventario` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) NOT NULL,
  `id_sucursal` int(11) NOT NULL,
  `cantidad_disponible` int(11) NOT NULL DEFAULT 0,
  `lote` varchar(50) DEFAULT NULL,
  `fecha_ingreso` date DEFAULT NULL,
  `fecha_caducidad` date DEFAULT NULL,
  `ubicacion_almacen` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_inventario`),
  KEY `fk_inventario_producto` (`id_producto`),
  KEY `fk_inventario_sucursal` (`id_sucursal`),
  CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `fk_inventario_sucursal` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursal` (`id_sucursal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_conversion_divisa`
--

DROP TABLE IF EXISTS `log_conversion_divisa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_conversion_divisa` (
  `id_log` int(11) NOT NULL AUTO_INCREMENT,
  `fecha_consulta` timestamp NOT NULL DEFAULT current_timestamp(),
  `valor_dolar` decimal(10,2) DEFAULT NULL,
  `fuente` enum('mindicador','banco_central') NOT NULL,
  `exito` tinyint(1) NOT NULL,
  `error_msg` text DEFAULT NULL,
  PRIMARY KEY (`id_log`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_conversion_divisa`
--

LOCK TABLES `log_conversion_divisa` WRITE;
/*!40000 ALTER TABLE `log_conversion_divisa` DISABLE KEYS */;
/*!40000 ALTER TABLE `log_conversion_divisa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orden`
--

DROP TABLE IF EXISTS `orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden` (
  `id_orden` int(11) NOT NULL AUTO_INCREMENT,
  `id_agricultor` int(11) NOT NULL,
  `id_asesor_asignado` int(11) DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` enum('cotizacion','pendiente_credito','aprobada','pago_pendiente','pago_confirmado','preparacion','despachada','entregada','cancelada') DEFAULT 'cotizacion',
  `tipo_entrega` enum('retiro_programado','despacho_predio') NOT NULL,
  `id_sucursal_retiro` int(11) DEFAULT NULL,
  `direccion_despacho` text DEFAULT NULL,
  `fecha_programada_retiro` datetime DEFAULT NULL,
  `metodo_pago` enum('transferencia_webpay','credito_agricola','factura_30dias') NOT NULL,
  `total_clp` decimal(12,2) NOT NULL,
  `total_usd_base` decimal(10,2) NOT NULL,
  `tipo_cambio_aplicado` decimal(10,2) NOT NULL,
  `webpay_token` varchar(255) DEFAULT NULL,
  `webpay_status` enum('inicial','aprobado','rechazado') DEFAULT 'inicial',
  `webpay_callback_recibido` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id_orden`),
  KEY `fk_orden_agricultor` (`id_agricultor`),
  KEY `fk_orden_asesor` (`id_asesor_asignado`),
  KEY `fk_orden_sucursal` (`id_sucursal_retiro`),
  CONSTRAINT `fk_orden_agricultor` FOREIGN KEY (`id_agricultor`) REFERENCES `agricultor` (`id_agricultor`),
  CONSTRAINT `fk_orden_asesor` FOREIGN KEY (`id_asesor_asignado`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_orden_sucursal` FOREIGN KEY (`id_sucursal_retiro`) REFERENCES `sucursal` (`id_sucursal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orden`
--

LOCK TABLES `orden` WRITE;
/*!40000 ALTER TABLE `orden` DISABLE KEYS */;
/*!40000 ALTER TABLE `orden` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago_registro`
--

DROP TABLE IF EXISTS `pago_registro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago_registro` (
  `id_pago` int(11) NOT NULL AUTO_INCREMENT,
  `id_orden` int(11) NOT NULL,
  `monto` decimal(12,2) NOT NULL,
  `fecha_pago` timestamp NOT NULL DEFAULT current_timestamp(),
  `transaccion_id_externo` varchar(255) DEFAULT NULL,
  `estado_pago` enum('iniciado','aprobado','rechazado','anulado') NOT NULL,
  `webhook_recibido` timestamp NULL DEFAULT NULL,
  `id_cobranzas_confirma` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `fk_pago_orden` (`id_orden`),
  KEY `fk_pago_cobranzas` (`id_cobranzas_confirma`),
  CONSTRAINT `fk_pago_cobranzas` FOREIGN KEY (`id_cobranzas_confirma`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_pago_orden` FOREIGN KEY (`id_orden`) REFERENCES `orden` (`id_orden`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago_registro`
--

LOCK TABLES `pago_registro` WRITE;
/*!40000 ALTER TABLE `pago_registro` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago_registro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `preparacion_despacho`
--

DROP TABLE IF EXISTS `preparacion_despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `preparacion_despacho` (
  `id_preparacion` int(11) NOT NULL AUTO_INCREMENT,
  `id_orden` int(11) NOT NULL,
  `id_jefe_almacen` int(11) NOT NULL,
  `fecha_preparacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_carga_camion` datetime DEFAULT NULL,
  `numero_guia` varchar(50) DEFAULT NULL,
  `observaciones` text DEFAULT NULL,
  PRIMARY KEY (`id_preparacion`),
  KEY `fk_prep_orden` (`id_orden`),
  KEY `fk_prep_almacen` (`id_jefe_almacen`),
  CONSTRAINT `fk_prep_almacen` FOREIGN KEY (`id_jefe_almacen`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_prep_orden` FOREIGN KEY (`id_orden`) REFERENCES `orden` (`id_orden`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `preparacion_despacho`
--

LOCK TABLES `preparacion_despacho` WRITE;
/*!40000 ALTER TABLE `preparacion_despacho` DISABLE KEYS */;
/*!40000 ALTER TABLE `preparacion_despacho` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_producto` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `categoria` varchar(255) NOT NULL,
  `precio_base_usd` double NOT NULL,
  `precio_clp_cache` double DEFAULT NULL,
  `tipo_cambio_usado` double DEFAULT NULL,
  `fecha_actualizacion_precio` datetime DEFAULT NULL,
  `ficha_tecnica_url` varchar(255) DEFAULT NULL,
  `peso_kg` double DEFAULT NULL,
  `requiere_aprobacion_credito` tinyint(1) DEFAULT 0,
  `activo` tinyint(1) DEFAULT 1,
  `url_imagen_producto` varchar(255) DEFAULT NULL,
  `stock_fisico` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_producto` (`codigo_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'DRON-T40','Dron Agrícola DJI T40','Dron de fumigación de 40 litros','dron',14500,13775000,950,NULL,NULL,NULL,1,1,'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScIPVadd-T5fvSQwRcNtxGNaPfdokvxxTAsQ&s',5),(2,'SENS-HUM','Sensor de Humedad IoT','Sensor para medición profunda de suelo','sensor',120,114000,950,NULL,NULL,NULL,0,1,'https://www.solen.cl/wp-content/uploads/2024/02/suelos_WET-2.webp',NULL),(3,'VALV-02','Válvula de Riego 2 Pulgadas','Repuesto para sistemas automatizados','repuesto',85,80750,950,NULL,NULL,NULL,0,1,NULL,NULL),(9,'DRON-M3M','Dron DJI Mavic 3 Multispectral','Dron con cámara multiespectral para análisis de salud de cultivos y topografía agrícola.','dron',4200,3990000,950,NULL,NULL,NULL,1,1,'https://images.pexels.com/photos/7382229/pexels-photo-7382229.jpeg?auto=compress&cs=tinysrgb&w=500',NULL),(10,'REP-BOMBA','Bomba de Agua Solar 3HP','Bomba sumergible para pozos profundos, compatible con paneles solares.','repuesto',1200,1140000,950,NULL,NULL,NULL,1,1,'https://images.pexels.com/photos/1626048/pexels-photo-1626048.jpeg?auto=compress&cs=tinysrgb&w=500',NULL);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sucursal`
--

DROP TABLE IF EXISTS `sucursal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sucursal` (
  `id_sucursal` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `region` varchar(100) DEFAULT NULL,
  `comuna` varchar(100) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `responsable_nombre` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_sucursal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sucursal`
--

LOCK TABLES `sucursal` WRITE;
/*!40000 ALTER TABLE `sucursal` DISABLE KEYS */;
INSERT INTO `sucursal` (`id_sucursal`, `nombre`, `region`, `comuna`, `direccion`, `telefono`, `responsable_nombre`) VALUES
(1, 'Sucursal Central', 'Región Metropolitana', 'Santiago', 'Av. Principal 123', '+56912345678', 'Juan Pérez'),
(2, 'Sucursal Sur', 'Región del Biobío', 'Concepción', 'Calle Comercio 456', '+56987654321', 'María González');
/*!40000 ALTER TABLE `sucursal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(150) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasena_hash` varchar(255) NOT NULL,
  `rol` enum('administrador_zona','asesor_tecnico','jefe_almacen','encargado_cobranzas','agricultor') NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1,
  `id_sucursal` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_sucursal` (`id_sucursal`),
  CONSTRAINT `fk_usuario_sucursal` FOREIGN KEY (`id_sucursal`) REFERENCES `sucursal` (`id_sucursal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20  3:19:23
