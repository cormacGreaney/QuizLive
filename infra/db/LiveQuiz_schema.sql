-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: livequiz
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--


--
-- Table structure for table `participant`
--

DROP TABLE IF EXISTS `participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participant` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_session_id` bigint unsigned NOT NULL,
  `user_id` bigint unsigned DEFAULT NULL,
  `nickname` varchar(50) NOT NULL,
  `joined_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_seen_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_participant_name` (`quiz_session_id`,`nickname`),
  KEY `idx_participant_session` (`quiz_session_id`,`joined_at`),
  KEY `idx_participant_user` (`user_id`),
  CONSTRAINT `fk_participant_session` FOREIGN KEY (`quiz_session_id`) REFERENCES `quiz_session` (`id`),
  CONSTRAINT `fk_participant_user` FOREIGN KEY (`user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participant`
--

LOCK TABLES `participant` WRITE;
/*!40000 ALTER TABLE `participant` DISABLE KEYS */;
/*!40000 ALTER TABLE `participant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participant_score`
--

DROP TABLE IF EXISTS `participant_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participant_score` (
  `participant_id` bigint unsigned NOT NULL,
  `quiz_session_id` bigint unsigned NOT NULL,
  `total_points` int NOT NULL DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`participant_id`),
  KEY `idx_ps_session` (`quiz_session_id`,`total_points` DESC),
  CONSTRAINT `fk_ps_participant` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`id`),
  CONSTRAINT `fk_ps_session` FOREIGN KEY (`quiz_session_id`) REFERENCES `quiz_session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participant_score`
--

LOCK TABLES `participant_score` WRITE;
/*!40000 ALTER TABLE `participant_score` DISABLE KEYS */;
/*!40000 ALTER TABLE `participant_score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint unsigned NOT NULL,
  `type` enum('MCQ_SINGLE','MCQ_MULTI','TRUE_FALSE','NUMERIC') NOT NULL DEFAULT 'MCQ_SINGLE',
  `text` text NOT NULL,
  `correct_payload` json DEFAULT NULL,
  `points` int NOT NULL DEFAULT '1',
  `ord` int NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question_quiz` (`quiz_id`,`is_deleted`,`ord`),
  CONSTRAINT `fk_question_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_option`
--

DROP TABLE IF EXISTS `question_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_option` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `question_id` bigint unsigned NOT NULL,
  `option_key` char(1) NOT NULL,
  `text` text NOT NULL,
  `ord` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_option` (`question_id`,`option_key`),
  KEY `idx_qo_question` (`question_id`),
  CONSTRAINT `fk_qo_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_option`
--

LOCK TABLES `question_option` WRITE;
/*!40000 ALTER TABLE `question_option` DISABLE KEYS */;
/*!40000 ALTER TABLE `question_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz`
--

DROP TABLE IF EXISTS `quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `owner_user_id` bigint unsigned DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_quiz_owner` (`owner_user_id`,`is_deleted`),
  CONSTRAINT `fk_quiz_owner` FOREIGN KEY (`owner_user_id`) REFERENCES `user_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz`
--

LOCK TABLES `quiz` WRITE;
/*!40000 ALTER TABLE `quiz` DISABLE KEYS */;
/*!40000 ALTER TABLE `quiz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_session`
--

DROP TABLE IF EXISTS `quiz_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_session` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_id` bigint unsigned NOT NULL,
  `host_user_id` bigint unsigned DEFAULT NULL,
  `status` enum('DRAFT','SCHEDULED','LIVE','PAUSED','ENDED') NOT NULL DEFAULT 'DRAFT',
  `public_code` varchar(12) NOT NULL,
  `show_leaderboard` tinyint(1) NOT NULL DEFAULT '1',
  `start_at` timestamp NULL DEFAULT NULL,
  `end_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_code` (`public_code`),
  KEY `idx_session_quiz` (`quiz_id`,`status`),
  KEY `fk_session_host` (`host_user_id`),
  CONSTRAINT `fk_session_host` FOREIGN KEY (`host_user_id`) REFERENCES `user_account` (`id`),
  CONSTRAINT `fk_session_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_session`
--

LOCK TABLES `quiz_session` WRITE;
/*!40000 ALTER TABLE `quiz_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `quiz_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score_event`
--

DROP TABLE IF EXISTS `score_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `score_event` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_session_id` bigint unsigned NOT NULL,
  `participant_id` bigint unsigned NOT NULL,
  `session_question_id` bigint unsigned NOT NULL,
  `delta_points` int NOT NULL,
  `reason` enum('CORRECT','PARTIAL','SPEED_BONUS','MANUAL_ADJ') NOT NULL DEFAULT 'CORRECT',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_scoree_session_participant` (`quiz_session_id`,`participant_id`),
  KEY `idx_scoree_sq` (`session_question_id`),
  KEY `fk_se_participant` (`participant_id`),
  CONSTRAINT `fk_se_participant` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`id`),
  CONSTRAINT `fk_se_session` FOREIGN KEY (`quiz_session_id`) REFERENCES `quiz_session` (`id`),
  CONSTRAINT `fk_se_sq` FOREIGN KEY (`session_question_id`) REFERENCES `session_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score_event`
--

LOCK TABLES `score_event` WRITE;
/*!40000 ALTER TABLE `score_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `score_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `session_question`
--

DROP TABLE IF EXISTS `session_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session_question` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_session_id` bigint unsigned NOT NULL,
  `question_id` bigint unsigned NOT NULL,
  `ord` int NOT NULL,
  `opened_at` timestamp NULL DEFAULT NULL,
  `closed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_question` (`quiz_session_id`,`question_id`),
  KEY `idx_sq_session` (`quiz_session_id`,`ord`),
  KEY `fk_sq_question` (`question_id`),
  CONSTRAINT `fk_sq_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
  CONSTRAINT `fk_sq_session` FOREIGN KEY (`quiz_session_id`) REFERENCES `quiz_session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `session_question`
--

LOCK TABLES `session_question` WRITE;
/*!40000 ALTER TABLE `session_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `session_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `quiz_session_id` bigint unsigned NOT NULL,
  `session_question_id` bigint unsigned NOT NULL,
  `participant_id` bigint unsigned NOT NULL,
  `answer_payload` json NOT NULL,
  `answered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `latency_ms` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_one_answer` (`session_question_id`,`participant_id`),
  KEY `idx_sub_by_session` (`quiz_session_id`,`session_question_id`),
  KEY `fk_sub_participant` (`participant_id`),
  CONSTRAINT `fk_sub_participant` FOREIGN KEY (`participant_id`) REFERENCES `participant` (`id`),
  CONSTRAINT `fk_sub_session` FOREIGN KEY (`quiz_session_id`) REFERENCES `quiz_session` (`id`),
  CONSTRAINT `fk_sub_sq` FOREIGN KEY (`session_question_id`) REFERENCES `session_question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_account`
--

DROP TABLE IF EXISTS `user_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_account` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(320) NOT NULL,
  `display_name` varchar(120) DEFAULT NULL,
  `role` enum('ADMIN','PLAYER') NOT NULL DEFAULT 'PLAYER',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_account`
--

LOCK TABLES `user_account` WRITE;
/*!40000 ALTER TABLE `user_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_leaderboard`
--

DROP TABLE IF EXISTS `v_leaderboard`;
/*!50001 DROP VIEW IF EXISTS `v_leaderboard`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_leaderboard` AS SELECT 
 1 AS `quiz_session_id`,
 1 AS `participant_id`,
 1 AS `nickname`,
 1 AS `total_points`,
 1 AS `updated_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_leaderboard`
--

/*!50001 DROP VIEW IF EXISTS `v_leaderboard`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_leaderboard` AS select `ps`.`quiz_session_id` AS `quiz_session_id`,`p`.`id` AS `participant_id`,`p`.`nickname` AS `nickname`,`ps`.`total_points` AS `total_points`,`ps`.`updated_at` AS `updated_at` from (`participant_score` `ps` join `participant` `p` on((`p`.`id` = `ps`.`participant_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-09 21:18:14
