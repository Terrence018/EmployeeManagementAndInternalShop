-- MySQL dump 10.13  Distrib 9.4.0, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: emp_mall
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主鍵ID',
  `emp_id` int NOT NULL COMMENT '員工ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `quantity` int DEFAULT '1' COMMENT '數量',
  `create_time` datetime DEFAULT NULL COMMENT '加入時間',
  `update_time` datetime DEFAULT NULL COMMENT '更新時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emp_product` (`emp_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='購物車表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (31,3,1,1,'2025-12-19 12:30:25','2025-12-19 12:30:25'),(69,13,31,1,'2025-12-24 13:09:33','2025-12-24 13:09:33');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL COMMENT '關聯的工單ID',
  `sender_id` bigint NOT NULL COMMENT '發送者ID',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '發送者名稱',
  `content` text COMMENT '訊息內容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天訊息記錄表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,3,0,'金牌管理員','嗨','2025-12-24 11:57:21'),(2,6,0,'金牌管理員','嗨','2025-12-24 11:58:11'),(3,6,0,'沈喬莉','你好','2025-12-24 11:58:13'),(4,7,0,'沈喬莉','測試','2025-12-24 12:02:29'),(5,7,0,'金牌管理員','測試','2025-12-24 12:02:39'),(6,11,0,'金牌管理員','好','2025-12-24 12:08:44'),(7,13,0,'金牌管理員','已經修改完成。','2025-12-24 12:10:00'),(8,13,0,'沈喬莉','謝謝','2025-12-24 12:10:04'),(9,15,0,'沈喬莉','訂單問題','2025-12-24 12:14:37'),(10,15,0,'金牌管理員','處理中','2025-12-24 12:14:40'),(11,15,0,'金牌管理員','已經','2025-12-24 12:14:41'),(12,15,0,'金牌管理員','完成','2025-12-24 12:14:43'),(13,15,0,'金牌管理員','謝謝','2025-12-24 12:14:45'),(14,15,0,'沈喬莉','謝謝','2025-12-24 12:14:50'),(15,17,0,'金牌管理員','好','2025-12-24 12:17:32'),(16,17,0,'金牌管理員','已經修改完成。','2025-12-24 12:17:35'),(17,17,0,'沈喬莉','可以','2025-12-24 12:17:38'),(18,18,0,'金牌管理員','測試','2025-12-24 12:27:41'),(19,18,0,'金牌管理員','問題已經解決完成。','2025-12-24 12:28:06'),(20,18,0,'沈喬莉','了解，謝謝。','2025-12-24 12:28:12'),(21,20,0,'沈喬莉','123','2025-12-24 13:11:12'),(22,20,0,'沈喬莉','123','2025-12-24 13:11:13'),(23,20,0,'沈喬莉','123','2025-12-24 13:11:14'),(24,20,0,'沈喬莉','測試','2025-12-24 13:11:16');
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_session`
--

DROP TABLE IF EXISTS `chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '發起的使用者ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '發起者名稱(快照)',
  `admin_id` bigint DEFAULT NULL COMMENT '負責的管理員ID (NULL代表未接單)',
  `category` varchar(50) DEFAULT NULL COMMENT '問題分類: 修改資料/訂單問題/其他',
  `topic` varchar(255) DEFAULT NULL COMMENT '問題描述摘要',
  `status` int DEFAULT '0' COMMENT '狀態: 0-等待中, 1-進行中, 2-已結束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服工單會話表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
INSERT INTO `chat_session` VALUES (1,13,'沈喬莉',NULL,'個人資料','測試修改個人資料',2,'2025-12-24 11:43:45','2025-12-24 12:01:01'),(2,13,'沈喬莉',NULL,'訂單','測試',2,'2025-12-24 11:44:33','2025-12-24 12:00:57'),(3,13,'沈喬莉',NULL,'個人資料','測試',2,'2025-12-24 11:46:08','2025-12-24 11:57:34'),(4,13,'沈喬莉',NULL,'個人資料','測試',2,'2025-12-24 11:47:11','2025-12-24 12:00:48'),(5,13,'沈喬莉',NULL,'個人資料','嗨，測試～',2,'2025-12-24 11:51:06','2025-12-24 12:00:51'),(6,13,'沈喬莉',1,'積分','請協助我處理積分/兌換問題。',1,'2025-12-24 11:57:56',NULL),(7,13,'沈喬莉',NULL,'訂單','嗨',2,'2025-12-24 12:02:19','2025-12-24 12:02:41'),(8,13,'沈喬莉',NULL,'訂單','哈哈哈',2,'2025-12-24 12:03:54','2025-12-24 12:04:00'),(9,13,'沈喬莉',NULL,'個人資料','你好',2,'2025-12-24 12:04:14','2025-12-24 12:05:06'),(10,13,'沈喬莉',1,'訂單','測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試測試',1,'2025-12-24 12:05:22',NULL),(11,13,'沈喬莉',NULL,'訂單','你好\n',2,'2025-12-24 12:08:40','2025-12-24 12:08:52'),(12,13,'沈喬莉',NULL,'訂單','訂單需要修正。\n',2,'2025-12-24 12:09:13','2025-12-24 12:09:23'),(13,13,'沈喬莉',NULL,'訂單','訂單需要修正\n',2,'2025-12-24 12:09:31','2025-12-24 12:12:48'),(14,13,'沈喬莉',1,'訂單','測試測試測試123132',1,'2025-12-24 12:13:08',NULL),(15,13,'沈喬莉',NULL,'訂單','測試測試',2,'2025-12-24 12:14:27','2025-12-24 12:14:55'),(16,13,'沈喬莉',1,'個人資料','嘿嘿嘿',1,'2025-12-24 12:16:15',NULL),(17,13,'沈喬莉',NULL,'個人資料','我需要修改個人資料\n',2,'2025-12-24 12:17:23','2025-12-24 12:17:48'),(18,13,'沈喬莉',NULL,'訂單','嗨',2,'2025-12-24 12:27:38','2025-12-24 12:28:16'),(19,13,'沈喬莉',NULL,'個人資料','嗨\n',2,'2025-12-24 13:07:12','2025-12-24 13:07:28'),(20,13,'沈喬莉',NULL,'個人資料','嗨測試',2,'2025-12-24 13:11:04','2025-12-24 13:11:19'),(21,13,'沈喬莉',NULL,'訂單','測試',2,'2025-12-24 13:11:33','2025-12-24 13:11:37'),(22,13,'沈喬莉',NULL,'個人資料','測試',2,'2025-12-24 13:16:35','2025-12-24 13:16:42'),(23,13,'沈喬莉',NULL,'積分','測試',2,'2025-12-24 13:17:02','2025-12-24 13:17:11'),(24,13,'沈喬莉',NULL,'積分','測試',2,'2025-12-24 13:27:35','2025-12-24 13:27:54');
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dept`
--

DROP TABLE IF EXISTS `dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dept` (
  `name` varchar(10) NOT NULL COMMENT '部門名稱',
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主鍵',
  `create_time` datetime DEFAULT NULL COMMENT '創建時間',
  `update_time` datetime DEFAULT NULL COMMENT '修改時間',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部門表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dept`
--

LOCK TABLES `dept` WRITE;
/*!40000 ALTER TABLE `dept` DISABLE KEYS */;
INSERT INTO `dept` VALUES ('人力部',1,'2025-12-17 10:04:56','2025-12-17 10:04:56'),('資訊部',2,'2025-12-17 10:04:56','2025-12-17 10:04:56'),('業務部',3,'2025-12-17 10:04:56','2025-12-17 10:04:56'),('行銷部',4,'2025-12-17 10:04:56','2025-12-17 10:04:56'),('行政部',5,'2025-12-17 10:04:56','2025-12-17 10:04:56'),('財務部',7,'2025-12-17 10:46:21','2025-12-17 10:46:21'),('小吃部',9,'2025-12-17 15:50:14','2025-12-17 15:50:14'),('系統管理部',11,'2025-12-19 11:30:02','2025-12-19 11:30:02');
/*!40000 ALTER TABLE `dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emp`
--

DROP TABLE IF EXISTS `emp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emp` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID,主鍵',
  `username` varchar(20) NOT NULL COMMENT '帳號(使用者名稱)',
  `password` varchar(32) DEFAULT '123456' COMMENT '密碼',
  `name` varchar(10) NOT NULL COMMENT '姓名',
  `gender` tinyint unsigned NOT NULL COMMENT '性别, 1:男, 2:女',
  `phone` char(11) NOT NULL COMMENT '電話號碼',
  `job` varchar(50) DEFAULT NULL COMMENT '職位',
  `role` tinyint unsigned NOT NULL DEFAULT '2' COMMENT '系統角色: 1 管理員, 2 普通員工',
  `salary` int unsigned DEFAULT NULL COMMENT '薪水',
  `points` int unsigned NOT NULL DEFAULT '0' COMMENT '員工持有點數',
  `image` varchar(255) DEFAULT NULL COMMENT '個人圖片',
  `entry_date` date DEFAULT NULL COMMENT '入職日期',
  `dept_id` int unsigned DEFAULT NULL COMMENT '部門ID',
  `create_time` datetime DEFAULT NULL COMMENT '創建時間',
  `update_time` datetime DEFAULT NULL COMMENT '修改時間',
  `email` varchar(100) DEFAULT NULL COMMENT '電子信箱',
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `uk_emp_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='員工表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emp`
--

LOCK TABLES `emp` WRITE;
/*!40000 ALTER TABLE `emp` DISABLE KEYS */;
INSERT INTO `emp` VALUES (1,'admin','123456','金牌管理員',1,'13800000000','管理員',1,15000,1000,'https://ui-avatars.com/api/?name=Admin&background=0D8ABC&color=fff','2023-01-01',11,'2025-12-17 10:04:56','2025-12-17 10:04:56','total.wombat.avqd@protectsmail.net'),(2,'user1','123456','科比布萊恩',1,'0909090909','籃球員',2,8000,2610,'https://ui-avatars.com/api/?name=ZW&background=random','2023-03-05',3,'2025-12-17 10:04:56','2025-12-18 10:26:16','sure.dingo.ivqx@protectsmail.net'),(3,'user2','123456','勒布朗詹姆士',1,'13800000002','籃球員',2,12000,1140,'https://ui-avatars.com/api/?name=ZM&background=random','2023-04-10',1,'2025-12-17 10:04:56','2025-12-17 10:04:56','nxnt8@airsworld.net'),(4,'user3','123456','唐納川普',2,'13800000003','會計',2,6000,10,'https://ui-avatars.com/api/?name=ZZ&background=random','2023-05-20',2,'2025-12-17 10:04:56','2025-12-17 14:16:29','i7gcf@airsworld.net'),(5,'user4','123456','馬一龍',1,'13800000004','資深工程師',2,7000,100,'https://ui-avatars.com/api/?name=SQ&background=random','2023-06-01',4,'2025-12-17 10:04:56','2025-12-17 10:04:56','ctbij@airsworld.net'),(13,'user222','123456','沈喬莉',2,'09090808','主管',2,50000,7300,'https://images.pexels.com/photos/35260728/pexels-photo-35260728.jpeg?_gl=1*kea6yd*_ga*MTgwNTcxODM5Ny4xNzY2Mzc2MzIz*_ga_8JE65Q40S6*czE3NjYzNzYzMjMkbzEkZzEkdDE3NjYzNzYzNzIkajExJGwwJGgw','2025-12-19',7,'2025-12-19 11:05:35','2025-12-23 12:11:41','useful.gamefowl.kbfy@protectsmail.net'),(16,'hr001','123456','陳怡君',2,'0910123456','人資經理',2,55000,1200,'https://fakeimg.pl/100x100/?text=Chen&font=noto','2023-05-12',1,'2025-12-22 15:22:20','2025-12-22 15:22:20','yijun.chen@company.com'),(17,'hr002','123456','林志豪',1,'0910123457','招募專員',2,42000,300,'https://fakeimg.pl/100x100/?text=Lin&font=noto','2024-02-10',1,'2025-12-22 15:22:20','2025-12-22 15:22:20','zhihao.lin@company.com'),(18,'hr003','123456','張雅婷',2,'0910123458','薪酬專員',2,45000,500,'https://fakeimg.pl/100x100/?text=Chang&font=noto','2023-11-01',1,'2025-12-22 15:22:20','2025-12-22 15:22:20','yating.chang@company.com'),(19,'it001','123456','黃建國',1,'0920111222','資深後端工程師',2,85000,2500,'https://fakeimg.pl/100x100/?text=Huang&font=noto','2022-03-15',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','jianguo.huang@company.com'),(20,'it002','123456','李淑芬',2,'0920111223','前端工程師',2,72000,1800,'https://fakeimg.pl/100x100/?text=Lee&font=noto','2023-07-20',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','shufen.lee@company.com'),(21,'it003','123456','王冠宇',1,'0920111224','全端工程師',2,78000,600,'https://fakeimg.pl/100x100/?text=Wang&font=noto','2023-09-10',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','guanyu.wang@company.com'),(22,'it004','123456','趙敏',2,'0920111225','UI/UX 設計師',2,65000,900,'https://fakeimg.pl/100x100/?text=Chao&font=noto','2024-01-05',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','min.chao@company.com'),(23,'it005','123456','劉偉強',1,'0920111226','DevOps 工程師',2,82000,120,'https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/1efabbbc-59f7-4718-8a2e-00c73513a593.png','2022-11-11',2,'2025-12-22 15:22:20','2025-12-22 23:20:17','weiqiang.liu@company.com'),(24,'it006','123456','吳柏翰',1,'0920111227','實習工程師',2,35000,50,'https://fakeimg.pl/100x100/?text=Wu&font=noto','2025-06-01',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','bohan.wu@company.com'),(25,'sale01','123456','蔡佳玲',2,'0930333444','業務經理',2,60000,5000,'https://fakeimg.pl/100x100/?text=Tsai&font=noto','2021-05-20',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','jialing.tsai@company.com'),(26,'sale02','123456','楊家豪',1,'0930333445','國內業務代表',2,40000,3200,'https://fakeimg.pl/100x100/?text=Yang&font=noto','2023-04-12',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','jiahao.yang@company.com'),(27,'sale03','123456','許婉婷',2,'0930333446','國外業務代表',2,48000,4100,'https://fakeimg.pl/100x100/?text=Hsu&font=noto','2022-08-30',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','wanting.hsu@company.com'),(28,'sale04','123456','鄭志強',1,'0930333447','業務助理',2,32000,800,'https://fakeimg.pl/100x100/?text=Cheng&font=noto','2024-09-15',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','zhiqiang.cheng@company.com'),(29,'mkt01','123456','謝欣怡',2,'0940555666','行銷企劃',2,46000,1500,'https://fakeimg.pl/100x100/?text=Hsieh&font=noto','2023-02-28',4,'2025-12-22 15:22:20','2025-12-22 15:22:20','xinyi.hsieh@company.com'),(30,'mkt02','123456','洪志明',1,'0940555667','社群小編',2,38000,200,'https://fakeimg.pl/100x100/?text=Hong&font=noto','2024-05-20',4,'2025-12-22 15:22:20','2025-12-22 15:22:20','zhiming.hong@company.com'),(31,'mkt03','123456','潘凱文',1,'0940555668','影音剪輯師',2,45000,600,'https://fakeimg.pl/100x100/?text=Pan&font=noto','2023-12-12',4,'2025-12-22 15:22:20','2025-12-22 15:22:20','kevin.pan@company.com'),(32,'adm01','123456','郭美惠',2,'0950777888','行政總務',2,36000,450,'https://fakeimg.pl/100x100/?text=Kuo&font=noto','2020-10-10',5,'2025-12-22 15:22:20','2025-12-22 15:22:20','meihui.kuo@company.com'),(33,'adm02','123456','羅建華',1,'0950777889','總機接待',2,30000,100,'https://fakeimg.pl/100x100/?text=Lo&font=noto','2025-01-01',5,'2025-12-22 15:22:20','2025-12-22 15:22:20','jianhua.lo@company.com'),(34,'fin01','123456','曾淑華',2,'0960999000','會計師',2,52000,900,'https://fakeimg.pl/100x100/?text=Tseng&font=noto','2021-06-30',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','shuhua.tseng@company.com'),(35,'fin02','123456','賴宗翰',1,'0960999001','出納專員',2,40000,350,'https://fakeimg.pl/100x100/?text=Lai&font=noto','2022-09-01',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','zonghan.lai@company.com'),(36,'food01','123456','阿基師',1,'0970111333','行政主廚',2,68000,8888,'https://fakeimg.pl/100x100/?text=Chef&font=noto','2019-01-01',7,'2025-12-22 15:22:20','2025-12-22 15:22:20','chef.super@company.com'),(37,'food02','123456','春嬌姨',2,'0970111334','二廚',2,45000,2000,'https://fakeimg.pl/100x100/?text=Auntie&font=noto','2020-03-15',7,'2025-12-22 15:22:20','2025-12-22 15:22:20','chunjiao@company.com'),(38,'food03','123456','志明哥',1,'0970111335','餐飲服務員',2,32000,1500,'https://fakeimg.pl/100x100/?text=Brother&font=noto','2024-07-01',7,'2025-12-22 15:22:20','2025-12-22 15:22:20','zhiming.food@company.com'),(39,'sys01','123456','駭客小寶',1,'0980222444','資安工程師',2,90000,0,'https://fakeimg.pl/100x100/?text=Hacker&font=noto','2023-11-11',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','baby.hacker@company.com'),(40,'sys02','123456','網管阿倫',1,'0980222445','網路管理員',2,55000,300,'https://fakeimg.pl/100x100/?text=Allen&font=noto','2022-04-01',2,'2025-12-22 15:22:20','2025-12-22 15:22:20','allen.net@company.com'),(41,'it007','123456','周杰倫',1,'0999888777','資料科學家',2,95000,3000,'https://fakeimg.pl/100x100/?text=Jay&font=noto','2023-01-18',11,'2025-12-22 15:22:20','2025-12-22 15:22:20','jay.chou@company.com'),(42,'sale05','123456','蔡依林',2,'0999888776','超業顧問',2,88000,6666,'https://fakeimg.pl/100x100/?text=Jolin&font=noto','2023-05-20',3,'2025-12-22 15:22:20','2025-12-22 15:22:20','jolin.tsai@company.com'),(43,'hr004','123456','五月天',1,'0999888775','團康活動專員',2,40000,1000,'https://fakeimg.pl/100x100/?text=Mayday&font=noto','2024-03-29',1,'2025-12-22 15:22:20','2025-12-22 15:22:20','mayday@company.com');
/*!40000 ALTER TABLE `emp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emp_expr`
--

DROP TABLE IF EXISTS `emp_expr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emp_expr` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主鍵',
  `emp_id` int unsigned DEFAULT NULL COMMENT '員工ID',
  `begin` date DEFAULT NULL COMMENT '開始時間',
  `end` date DEFAULT NULL COMMENT '結束時間',
  `company` varchar(50) DEFAULT NULL COMMENT '公司名稱',
  `job` varchar(50) DEFAULT NULL COMMENT '職位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工作經歷';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emp_expr`
--

LOCK TABLES `emp_expr` WRITE;
/*!40000 ALTER TABLE `emp_expr` DISABLE KEYS */;
INSERT INTO `emp_expr` VALUES (3,3,'2021-06-01','2023-03-01','微軟','產品經理'),(11,4,'2022-01-01','2023-05-01','Google','清潔師'),(16,2,'2020-01-01','2022-12-01','商研院','Java開發'),(17,2,'2022-12-01','2023-03-01','蝦皮','後端工程師'),(30,13,'2024-12-01','2025-12-02','Lush','入門'),(31,13,'2025-12-30','2025-12-15','McCann','正式');
/*!40000 ALTER TABLE `emp_expr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `emp_log`
--

DROP TABLE IF EXISTS `emp_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emp_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID, 主鍵',
  `emp_id` int unsigned NOT NULL COMMENT '員工ID',
  `operate_time` datetime DEFAULT NULL COMMENT '操作時間',
  `info` varchar(2000) DEFAULT NULL COMMENT '日誌資訊',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='員工日誌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `emp_log`
--

LOCK TABLES `emp_log` WRITE;
/*!40000 ALTER TABLE `emp_log` DISABLE KEYS */;
INSERT INTO `emp_log` VALUES (24,1,'2025-12-17 20:11:27','新增員工：派大星'),(25,1,'2025-12-17 20:23:43','編輯員工：派大星'),(26,1,'2025-12-18 10:05:14','編輯員工：科比布萊恩'),(28,2,'2025-12-18 10:59:32','員工【科比布萊恩】自行修改個人資料：變更了登入密碼'),(29,1,'2025-12-18 15:03:00','新增員工：麥克喬丹'),(30,1,'2025-12-19 11:05:35','新增員工：沈喬莉'),(31,1,'2025-12-19 11:06:59','編輯員工：沈喬莉'),(32,1,'2025-12-19 11:08:25','批量刪除員工，ID列表：[11]'),(33,1,'2025-12-19 11:17:55','刪除員工：岳不群'),(34,1,'2025-12-19 11:20:14','新增員工：馬爾卡寧'),(35,1,'2025-12-19 11:21:38','新增員工：凱文杜蘭特'),(36,1,'2025-12-19 11:21:52','批量刪除員工：馬爾卡寧, 凱文杜蘭特'),(37,1,'2025-12-22 12:03:30','編輯員工：沈喬莉'),(38,1,'2025-12-22 12:06:27','編輯員工：沈喬莉'),(39,1,'2025-12-22 12:13:19','刪除員工：派大星'),(40,1,'2025-12-22 20:53:16','編輯員工：沈喬莉'),(41,13,'2025-12-22 20:56:44','員工自行修改個人資料'),(42,13,'2025-12-22 21:02:02','員工自行修改資料：手機:[09090707→09090808] '),(43,1,'2025-12-22 21:02:41','管理員修改員工 [沈喬莉] 資料：[密碼已變更] '),(44,13,'2025-12-22 21:06:32','員工自行修改資料：[密碼已變更] '),(45,1,'2025-12-22 22:58:37','管理員修改員工 [劉偉強] (無基本資料變更)'),(46,1,'2025-12-22 22:59:50','管理員修改員工 [劉偉強] (無基本資料變更)'),(47,1,'2025-12-22 23:03:11','管理員修改員工 [劉偉強] (無基本資料變更)'),(48,1,'2025-12-22 23:03:34','管理員修改員工 [劉偉強] (無基本資料變更)'),(49,1,'2025-12-22 23:07:45','管理員修改員工 [劉偉強] (無基本資料變更)'),(50,1,'2025-12-22 23:20:17','管理員修改員工 [劉偉強] 資料：[頭像已更新] '),(51,13,'2025-12-23 12:11:41','員工自行修改資料 (無內容變更)'),(52,1,'2025-12-23 15:04:41','新增員工：陳土水'),(53,1,'2025-12-23 15:04:52','管理員修改員工 [陳土水] 資料：[頭像已更新] '),(54,1,'2025-12-23 15:30:19','刪除員工：陳土水'),(55,1,'2025-12-23 15:32:22','新增員工：陳土水'),(56,1,'2025-12-23 15:32:29','管理員修改員工 [陳土水] 資料：薪資:[30000→22000] '),(57,1,'2025-12-23 15:32:31','刪除員工：陳土水');
/*!40000 ALTER TABLE `emp_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operate_log`
--

DROP TABLE IF EXISTS `operate_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operate_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `operate_emp_id` int unsigned DEFAULT NULL COMMENT '操作人ID',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `class_name` varchar(100) DEFAULT NULL COMMENT '操作的類名',
  `method_name` varchar(100) DEFAULT NULL COMMENT '操作的方法名',
  `method_params` varchar(2000) DEFAULT NULL COMMENT '方法參數',
  `return_value` varchar(2000) DEFAULT NULL COMMENT '返回值',
  `cost_time` bigint unsigned DEFAULT NULL COMMENT '方法執行耗時,單位：ms',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日誌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operate_log`
--

LOCK TABLES `operate_log` WRITE;
/*!40000 ALTER TABLE `operate_log` DISABLE KEYS */;
INSERT INTO `operate_log` VALUES (35,1,'2025-12-17 15:50:14','com.empmall.controller.DeptController','add','[Dept(id=null, name=小吃部, createTime=2025-12-17T15:50:14.052868, updateTime=2025-12-17T15:50:14.052890)]','Result(code=1, msg=success, data=null)',11),(36,1,'2025-12-19 10:37:41','com.empmall.controller.DeptController','add','[Dept(id=null, name=工業部, createTime=2025-12-19T10:37:41.312479, updateTime=2025-12-19T10:37:41.312493)]','Result(code=1, msg=success, data=null)',27),(37,1,'2025-12-19 10:37:54','com.empmall.controller.DeptController','delete','[10]','Result(code=1, msg=success, data=null)',6),(38,1,'2025-12-19 11:30:02','com.empmall.controller.DeptController','add','[Dept(id=null, name=系統管理部, createTime=2025-12-19T11:30:02.126001, updateTime=2025-12-19T11:30:02.126039)]','Result(code=1, msg=success, data=null)',6),(39,1,'2025-12-22 14:02:07','com.empmall.controller.DeptController','add','[Dept(id=null, name=測試部門, createTime=2025-12-22T14:02:07.199490, updateTime=2025-12-22T14:02:07.200315)]','Result(code=1, msg=success, data=null)',26),(40,1,'2025-12-22 14:02:12','com.empmall.controller.DeptController','delete','[12]','Result(code=1, msg=success, data=null)',3),(41,1,'2025-12-22 14:03:59','com.empmall.controller.DeptController','add','[Dept(id=null, name=測試, createTime=2025-12-22T14:03:58.666219, updateTime=2025-12-22T14:03:58.666228)]','Result(code=1, msg=success, data=null)',11),(42,1,'2025-12-22 14:04:08','com.empmall.controller.DeptController','delete','[13]','Result(code=1, msg=success, data=null)',4),(43,1,'2025-12-22 14:04:22','com.empmall.controller.DeptController','add','[Dept(id=null, name=測試, createTime=2025-12-22T14:04:21.883840, updateTime=2025-12-22T14:04:21.883846)]','Result(code=1, msg=success, data=null)',7),(44,1,'2025-12-22 14:04:36','com.empmall.controller.DeptController','delete','[14]','Result(code=1, msg=success, data=null)',5),(45,1,'2025-12-22 14:04:42','com.empmall.controller.DeptController','add','[Dept(id=null, name=測試, createTime=2025-12-22T14:04:41.513711, updateTime=2025-12-22T14:04:41.513717)]','Result(code=1, msg=success, data=null)',5),(46,1,'2025-12-22 14:04:46','com.empmall.controller.DeptController','delete','[15]','Result(code=1, msg=success, data=null)',5),(47,1,'2025-12-23 15:29:52','com.empmall.controller.DeptController','add','[Dept(id=null, name=測試部門, createTime=2025-12-23T15:29:51.785935, updateTime=2025-12-23T15:29:51.785954)]','Result(code=1, msg=success, data=null)',12),(48,1,'2025-12-23 15:29:58','com.empmall.controller.DeptController','delete','[16]','Result(code=1, msg=success, data=null)',7);
/*!40000 ALTER TABLE `operate_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '明細ID',
  `order_id` int NOT NULL COMMENT '歸屬於哪張訂單 (orders.id)',
  `product_id` int NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名稱快照',
  `image` varchar(255) DEFAULT NULL COMMENT '圖片快照',
  `quantity` int DEFAULT '1' COMMENT '購買數量',
  `points_per_item` int NOT NULL COMMENT '單價點數快照',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='訂單明細表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (10,26,3,'小米手環','https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?auto=format&fit=crop&w=400&q=80',1,800),(11,26,6,'神秘小禮物','https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=400&q=80',1,50),(12,26,9,'微軟藍芽滑鼠','https://img.yec.tw/zp/MerchandiseImages/FDC82DD25C-SP-8037927.jpg',2,200),(13,27,4,'機械鍵盤','https://images.unsplash.com/photo-1587829741301-dc798b91add1?auto=format&fit=crop&w=400&q=80',1,2500),(14,28,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(15,29,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(16,29,5,'人體工學椅','https://images.unsplash.com/photo-1505843490538-5133c6c7d0e1?auto=format&fit=crop&w=400&q=80',1,5000),(17,30,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(18,30,3,'小米手環','https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?auto=format&fit=crop&w=400&q=80',1,800),(19,31,9,'微軟藍芽滑鼠','https://img.yec.tw/zp/MerchandiseImages/FDC82DD25C-SP-8037927.jpg',1,200),(20,31,4,'機械鍵盤','https://images.unsplash.com/photo-1587829741301-dc798b91add1?auto=format&fit=crop&w=400&q=80',1,2500),(21,32,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(22,32,3,'小米手環','https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?auto=format&fit=crop&w=400&q=80',1,800),(23,33,6,'神秘小禮物','https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=400&q=80',1,50),(24,33,9,'微軟藍芽滑鼠','https://img.yec.tw/zp/MerchandiseImages/FDC82DD25C-SP-8037927.jpg',1,200),(25,34,4,'機械鍵盤','https://images.unsplash.com/photo-1587829741301-dc798b91add1?auto=format&fit=crop&w=400&q=80',1,2500),(26,35,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(27,36,4,'機械鍵盤','https://images.pexels.com/photos/8288049/pexels-photo-8288049.jpeg?_gl=1*1ppvcbs*_ga*ODYxNzIyNjE1LjE3NjYzNzcxNjU.*_ga_8JE65Q40S6*czE3NjYzNzcxNjQkbzEkZzEkdDE3NjYzNzc0MTIkajUzJGwwJGgw',1,2500),(28,36,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(29,37,3,'小米手環','https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?auto=format&fit=crop&w=400&q=80',1,750),(30,37,1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80',1,200),(31,38,26,'極輕量自動折疊傘','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/970121f4-b121-4bd7-aec9-7e76e3705086.jpg',1,300),(32,38,30,'國家地理雜誌 一年期訂閱','https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=800',1,1800),(33,39,27,'316不鏽鋼保溫杯 500ml','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/b237b8e5-21fe-4edb-b6bc-15757c2acb7c.jpg',1,450),(34,40,28,'原子習慣 (暢銷經典)','https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=800',1,380),(35,41,20,'有機綜合堅果隨身包(6入)','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/7970c455-d9a9-44d2-b954-0153870eb8bb.jpg',1,250),(36,42,22,'王品集團 500元抵用券','https://images.unsplash.com/photo-1544148103-0773bf10d330?auto=format&fit=crop&q=80&w=800',1,500),(37,42,23,'Uber Eats 200元 序號','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/82e9a5b6-2f77-4609-be98-41da3ebdd5a0.jpeg',1,200),(38,43,16,'多功能鋁合金筆電支架','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/4733f9d0-7749-4b0d-b0f9-777a930070e6.jpg',1,850);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `emp_id` int unsigned NOT NULL COMMENT '下單員工ID',
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '訂單ID',
  `status` tinyint unsigned DEFAULT '1' COMMENT '訂單狀態: 1 待處理, 2 已出貨/可領取, 3 已完成, 4 已取消',
  `delivery_method` tinyint unsigned NOT NULL COMMENT '領取方式: 1 公司自取, 2 郵寄',
  `address` varchar(255) DEFAULT NULL COMMENT '郵寄地址 (自取則為null)',
  `create_time` datetime NOT NULL COMMENT '下單時間',
  `update_time` datetime NOT NULL COMMENT '最後更新時間',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '訂單總點數',
  PRIMARY KEY (`id`),
  KEY `fk_orders_emp` (`emp_id`),
  CONSTRAINT `fk_orders_emp` FOREIGN KEY (`emp_id`) REFERENCES `emp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兌換訂單表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (13,26,3,1,'','2025-12-19 12:16:53','2025-12-19 12:17:25',1250),(13,27,3,2,'基隆市台北路高雄街Taiwan號新竹樓','2025-12-19 12:18:29','2025-12-19 12:24:33',2500),(3,28,4,1,'','2025-12-19 12:30:33','2025-12-19 12:32:10',200),(13,29,4,2,'臺北市中山區民生東路三段67號','2025-12-19 13:08:37','2025-12-19 13:09:46',5200),(13,30,4,1,'','2025-12-19 13:22:00','2025-12-19 13:22:13',1000),(13,31,4,1,'','2025-12-19 13:26:10','2025-12-19 13:26:39',2700),(13,32,4,1,'','2025-12-20 22:11:19','2025-12-22 09:24:28',1000),(13,33,2,2,'新北市基隆路高雄區三重路1000號','2025-12-22 09:23:20','2025-12-22 21:58:55',250),(13,34,4,1,'','2025-12-22 09:23:31','2025-12-22 21:58:58',2500),(13,35,4,2,'106台灣臺北市大安區新生南路一段103巷4號','2025-12-22 21:58:42','2025-12-22 21:59:44',200),(13,36,4,1,'','2025-12-22 22:04:26','2025-12-22 22:07:05',2700),(13,38,2,2,'104台灣臺北市中山區復興北路280巷10弄1 樓3號','2025-12-23 15:40:31','2025-12-23 15:48:23',2100),(13,39,4,1,'','2025-12-23 15:41:41','2025-12-23 15:48:22',450),(13,40,4,1,'','2025-12-23 15:48:06','2025-12-23 15:48:20',380),(13,41,3,1,'','2025-12-23 15:52:17','2025-12-23 15:53:18',250),(13,42,4,1,'','2025-12-23 15:52:29','2025-12-23 15:53:06',700),(13,43,4,1,'','2025-12-23 15:52:42','2025-12-23 15:53:00',850);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_log`
--

DROP TABLE IF EXISTS `points_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `emp_id` int NOT NULL COMMENT '員工ID',
  `points` int NOT NULL COMMENT '變動數值',
  `balance` int DEFAULT NULL COMMENT '變動後的餘額 (選填)',
  `info` varchar(255) DEFAULT NULL COMMENT '備註',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '記錄時間',
  `operator_id` int unsigned DEFAULT NULL COMMENT '操作人ID (管理員)',
  `type` int DEFAULT '2' COMMENT '類型 1:增加 2:減少',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='點數流水帳表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_log`
--

LOCK TABLES `points_log` WRITE;
/*!40000 ALTER TABLE `points_log` DISABLE KEYS */;
INSERT INTO `points_log` VALUES (20,10,-850,750,'商城訂單消費(批量)','2025-12-18 21:05:00',10,2),(21,10,10000,10750,'測試使用','2025-12-18 21:13:02',1,1),(22,10,-7500,3250,'商城訂單消費(批量)','2025-12-18 21:14:02',10,2),(23,10,-1000,2250,'商城訂單消費(批量)','2025-12-18 21:16:03',10,2),(24,13,10000,10000,'入職獎勵','2025-12-19 11:35:46',1,1),(25,13,-1200,8750,'商城訂單消費(批量)','2025-12-19 12:16:53',13,2),(26,13,-2500,6250,'商城訂單消費(批量)','2025-12-19 12:18:29',13,2),(27,3,-200,1140,'商城兌換: 星巴克咖啡券','2025-12-19 12:30:33',3,2),(28,13,-5200,1050,'商城訂單消費(批量)','2025-12-19 13:08:37',13,2),(29,13,10000,11050,'補退款項','2025-12-19 13:21:27',1,1),(30,13,-1000,10050,'商城訂單消費(批量)','2025-12-19 13:22:00',13,2),(31,13,-2700,7350,'商城訂單消費(批量)','2025-12-19 13:26:10',13,2),(32,13,2700,10050,'訂單取消退款 (訂單號: 31)','2025-12-19 13:26:39',1,1),(33,13,-1000,9050,'商城訂單消費(批量)','2025-12-20 22:11:19',13,2),(34,2,10,2610,'測試','2025-12-20 22:14:11',1,1),(35,4,-10,0,'','2025-12-20 22:14:30',1,1),(36,13,-250,8800,'商城訂單消費','2025-12-22 09:23:20',13,2),(37,13,-2500,6300,'商城兌換: 機械鍵盤','2025-12-22 09:23:31',13,2),(38,13,1000,7300,'訂單取消退款 (訂單號: 32)','2025-12-22 09:24:28',1,1),(40,4,-10,10,'遲到逞罰','2025-12-22 14:15:13',1,1),(41,4,10,20,'測試','2025-12-22 14:16:09',1,1),(42,4,-10,10,'遲到懲罰','2025-12-22 14:16:21',1,1),(43,13,-200,7100,'商城兌換: 星巴克咖啡券','2025-12-22 21:58:41',13,2),(44,13,2500,9600,'訂單取消退款 (訂單號: 34)','2025-12-22 21:58:58',1,1),(45,13,200,9800,'訂單取消退款 (訂單號: 35)','2025-12-22 21:59:44',1,1),(46,13,-2700,7100,'商城兌換：購物車批量兌換','2025-12-22 22:04:26',13,2),(47,13,2700,9800,'訂單取消退款 (訂單號: 36)','2025-12-22 22:07:05',1,1),(48,13,-950,8850,'商城兌換：購物車批量兌換','2025-12-23 10:12:20',13,2),(49,13,1000,9850,'專案獎勵','2025-12-23 11:13:08',1,1),(50,13,-100,9750,'遲到扣點','2025-12-23 11:13:25',1,1),(51,13,-200,9950,'連續遲到','2025-12-23 13:04:08',1,1),(52,13,-200,9750,'連續遲到','2025-12-23 13:04:26',1,1),(53,13,-2100,7650,'商城兌換：購物車批量兌換','2025-12-23 15:40:31',13,2),(54,13,-450,7200,'商城兌換: 316不鏽鋼保溫杯 500ml','2025-12-23 15:41:41',13,2),(55,13,-380,6820,'商城兌換: 原子習慣 (暢銷經典)','2025-12-23 15:48:06',13,2),(56,13,380,7200,'訂單取消退款 (訂單號: 40)','2025-12-23 15:48:20',1,1),(57,13,450,7650,'訂單取消退款 (訂單號: 39)','2025-12-23 15:48:22',1,1),(58,13,-250,7400,'商城兌換: 有機綜合堅果隨身包(6入)','2025-12-23 15:52:17',13,2),(59,13,-700,6700,'商城兌換：購物車批量兌換','2025-12-23 15:52:29',13,2),(60,13,-850,5850,'商城兌換: 多功能鋁合金筆電支架','2025-12-23 15:52:42',13,2),(61,13,850,6700,'訂單取消退款 (訂單號: 43)','2025-12-23 15:53:00',1,1),(62,13,700,7400,'訂單取消退款 (訂單號: 42)','2025-12-23 15:53:06',1,1),(63,13,-100,7300,'測試','2025-12-23 16:00:17',1,1);
/*!40000 ALTER TABLE `points_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_log`
--

DROP TABLE IF EXISTS `product_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_log` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `operate_user` int unsigned DEFAULT NULL COMMENT '操作人ID',
  `operate_time` datetime DEFAULT NULL COMMENT '操作時間',
  `content` varchar(255) DEFAULT NULL COMMENT '日誌內容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商城業務日誌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_log`
--

LOCK TABLES `product_log` WRITE;
/*!40000 ALTER TABLE `product_log` DISABLE KEYS */;
INSERT INTO `product_log` VALUES (29,1,'2025-12-22 21:25:13','編輯商品 [小米手環]：所需點數:[800→750] '),(30,1,'2025-12-22 21:25:13','編輯商品 [小米手環] (無內容變更)'),(31,1,'2025-12-22 21:25:18','編輯商品 [星巴克咖啡券]：庫存:[43→45] '),(32,1,'2025-12-22 23:08:35','編輯商品 [機械鍵盤]：[圖片已更新] '),(33,1,'2025-12-22 23:21:09','編輯商品 [機械鍵盤]：[圖片已更新] '),(34,1,'2025-12-23 10:09:01','編輯商品 [星巴克咖啡券]：狀態:[上架→下架] '),(35,1,'2025-12-23 10:09:01','編輯商品 [星巴克咖啡券]：狀態:[下架→上架] '),(36,1,'2025-12-23 11:33:34','編輯商品 [威秀電影票]：狀態:[下架→上架] '),(37,1,'2025-12-23 11:33:34','編輯商品 [威秀電影票]：狀態:[上架→下架] '),(38,1,'2025-12-23 13:13:30','編輯商品 [哈利波特：全集典藏版]：[圖片已更新] '),(39,1,'2025-12-23 13:14:17','編輯商品 [316不鏽鋼保溫杯 500ml]：[圖片已更新] '),(40,1,'2025-12-23 13:16:23','編輯商品 [極輕量自動折疊傘]：[圖片已更新] '),(41,1,'2025-12-23 13:17:06','編輯商品 [MUJI 無印風香氛水氧機]：[圖片已更新] '),(42,1,'2025-12-23 13:17:34','編輯商品 [Uber Eats 200元 序號]：[圖片已更新] '),(43,1,'2025-12-23 13:18:36','編輯商品 [有機綜合堅果隨身包]：名稱:[有機綜合堅果隨身包→有機綜合堅果隨身包(6入)] [圖片已更新] '),(44,1,'2025-12-23 13:20:25','編輯商品 [莫蘭迪色系文件收納盒]：[圖片已更新] '),(45,1,'2025-12-23 13:21:56','編輯商品 [iPhone 17 Pro 128G]：名稱:[iPhone 17 Pro 128G→iPhone 17 Pro 128G(銀色)] [圖片已更新] '),(46,1,'2025-12-23 13:22:29','編輯商品 [Nintendo Switch OLED 款]：[圖片已更新] '),(47,1,'2025-12-23 13:23:41','編輯商品 [多功能鋁合金筆電支架]：[圖片已更新] '),(48,1,'2025-12-23 13:29:26','編輯商品 [微軟藍芽滑鼠]：[圖片已更新] '),(49,1,'2025-12-23 14:01:49','編輯商品 [威秀影城雙人電影套票]：[圖片已更新] '),(50,1,'2025-12-23 14:18:53','編輯商品 [哈根達斯-迷你杯兌換券]：[圖片已更新] '),(51,1,'2025-12-23 14:28:08','編輯商品 [哈根達斯-迷你杯兌換券]：分類:[食品飲料→電子票券] '),(52,1,'2025-12-23 15:30:46','刪除商品：哈根達斯-迷你杯兌換券'),(53,1,'2025-12-23 15:33:25','編輯商品 [羅技 MX Master 3S 無線滑鼠]：狀態:[上架→下架] '),(54,1,'2025-12-23 15:33:26','編輯商品 [羅技 MX Master 3S 無線滑鼠]：狀態:[下架→上架] '),(55,1,'2025-12-23 15:33:31','編輯商品 [羅技 MX Master 3S 無線滑鼠]：[描述已更新] '),(56,1,'2025-12-23 15:33:35','編輯商品 [羅技 MX Master 3S 無線滑鼠]：[描述已更新] '),(57,1,'2025-12-23 15:35:46','新增商品 [測試]'),(58,1,'2025-12-23 16:03:12','刪除商品：測試');
/*!40000 ALTER TABLE `product_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名稱',
  `image` varchar(300) DEFAULT NULL COMMENT '商品圖片URL',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述',
  `points_needed` int unsigned NOT NULL COMMENT '兌換所需點數',
  `status` tinyint unsigned DEFAULT '1' COMMENT '狀態: 1 上架, 0 下架',
  `create_time` datetime NOT NULL COMMENT '創建時間',
  `update_time` datetime NOT NULL COMMENT '更新時間',
  `category` int DEFAULT NULL COMMENT '分類: 1-3C, 2-辦公, 3-食品, 4-票券 5-生活百貨 6-圖書',
  `stock` int DEFAULT '50',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商城商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'星巴克咖啡券','https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80','電子兌換券，全台通用，面額150元',200,1,'2025-12-17 10:04:56','2025-12-23 10:09:01',4,44),(2,'威秀電影票','https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=400&q=80','通用電影票，不分平假日',300,0,'2025-12-17 10:04:56','2025-12-23 11:33:34',4,50),(3,'小米手環','https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?auto=format&fit=crop&w=400&q=80','健康監測，防水，長續航',750,1,'2025-12-17 10:04:56','2025-12-22 21:25:13',1,41),(4,'機械鍵盤','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/37f81cf0-07ba-441d-a965-7c1ee991be23.jpg','Cherry青軸，工程師必備',2500,1,'2025-12-17 10:04:56','2025-12-22 23:21:09',1,41),(5,'人體工學椅','https://images.unsplash.com/photo-1505843490538-5133c6c7d0e1?auto=format&fit=crop&w=400&q=80','保護腰椎，午休可躺',5000,1,'2025-12-17 10:04:56','2025-12-17 10:04:56',2,48),(6,'神秘小禮物','https://images.unsplash.com/photo-1549465220-1a8b9238cd48?auto=format&fit=crop&w=400&q=80','驚喜盲盒，隨機開出辦公小物',50,1,'2025-12-17 10:04:56','2025-12-17 10:04:56',3,43),(9,'微軟藍芽滑鼠','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/1812f36b-a84b-4c1e-a591-03dab04a3f27.jpg','微軟藍芽滑鼠',200,1,'2025-12-19 12:14:55','2025-12-23 13:29:26',1,57),(10,'iPhone 17 Pro 128G(銀色)','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/49204081-b67a-4364-bda1-8e75d61d22df.jpg','Apple 最新旗艦機型，鈦金屬邊框，A19 仿生晶片，極致效能體驗。',35000,1,'2025-12-23 13:10:47','2025-12-23 13:21:56',1,5),(11,'Sony WH-1000XM5 降噪耳機','https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&q=80&w=800','業界領先的降噪技術，30小時超長續航，沈浸式音質享受。',8500,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',1,15),(12,'Nintendo Switch OLED 款','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/3a9c1fdf-59f0-48b3-b879-e178a3a04dec.jpg','色彩更鮮豔的 7 吋 OLED 螢幕，支援桌上模式與電視模式，隨時隨地暢玩。',9800,1,'2025-12-23 13:10:47','2025-12-23 13:22:29',1,10),(13,'羅技 MX Master 3S 滑鼠','https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?auto=format&fit=crop&q=80&w=800','人體工學設計，8000 DPI 精準感測，靜音點擊，提升辦公效率神器。',3200,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',1,25),(14,'質感皮革辦公桌墊','https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&q=80&w=800','防水防刮 PU 皮革，超大尺寸，讓桌面井然有序，提升辦公質感。',450,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',2,60),(15,'LAMY 狩獵者鋼筆 (消光黑)','https://images.unsplash.com/photo-1585336261022-680e295ce3fe?auto=format&fit=crop&q=80&w=800','德國工藝設計，書寫流暢，人體工學握位，附贈吸墨器。',1200,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',2,30),(16,'多功能鋁合金筆電支架','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/4733f9d0-7749-4b0d-b0f9-777a930070e6.jpg','六段高度調節，加強散熱，改善頸椎疲勞，可折疊好攜帶。',850,1,'2025-12-23 13:10:47','2025-12-23 13:23:41',2,40),(17,'莫蘭迪色系文件收納盒','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/60e62284-4ba7-496b-96ac-ea2715d4a4ff.jpg','三層抽屜設計，大容量收納，告別桌面雜亂，辦公好心情。',350,1,'2025-12-23 13:10:47','2025-12-23 13:20:25',2,55),(18,'GODIVA 金裝巧克力禮盒','https://images.unsplash.com/photo-1549007994-cb92caebd54b?auto=format&fit=crop&q=80&w=800','比利時皇家御用巧克力，濃郁口感，送禮自用兩相宜。',1500,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',3,20),(19,'日本掛耳式濾泡咖啡 (24入)','https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&q=80&w=800','精選阿拉比卡豆，中深烘焙，香氣濃郁，早晨提神首選。',500,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',3,100),(20,'有機綜合堅果隨身包(6入)','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/7970c455-d9a9-44d2-b954-0153870eb8bb.jpg','低溫烘焙，保留營養，無添加防腐劑，健康的辦公室零食。',250,1,'2025-12-23 13:10:47','2025-12-23 13:18:36',3,79),(21,'威秀影城雙人電影套票','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/e7081ad2-d1ff-4a53-9bd7-273f4b264eb0.jpg','全台通用，含兩張電影票與爆米花飲料，假日不加價。',600,1,'2025-12-23 13:10:47','2025-12-23 14:01:49',4,50),(22,'王品集團 500元抵用券','https://images.unsplash.com/photo-1544148103-0773bf10d330?auto=format&fit=crop&q=80&w=800','適用旗下多品牌餐廳，聚餐慶祝首選，即買即用。',500,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',4,100),(23,'Uber Eats 200元 序號','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/82e9a5b6-2f77-4609-be98-41da3ebdd5a0.jpeg','美食外送抵用金，新舊戶皆可使用，加班晚餐好幫手。',200,1,'2025-12-23 13:10:47','2025-12-23 13:17:34',4,200),(24,'Dyson Supersonic 吹風機','https://images.unsplash.com/photo-1522338140262-f46f5913618a?auto=format&fit=crop&q=80&w=800','智能溫控，快速乾髮，呵護秀髮光澤，附多種吹嘴。',12000,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',5,8),(25,'MUJI 無印風香氛水氧機','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/20633f27-a402-4d70-a777-8bed59d49334.jpg','超音波震盪，細緻噴霧，暖色夜燈，營造放鬆居家氛圍。',1800,1,'2025-12-23 13:10:47','2025-12-23 13:17:06',5,25),(26,'極輕量自動折疊傘','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/970121f4-b121-4bd7-aec9-7e76e3705086.jpg','碳纖維傘骨，抗風性強，黑膠塗層防曬降溫，晴雨兩用。',300,1,'2025-12-23 13:10:47','2025-12-23 13:16:23',5,59),(27,'316不鏽鋼保溫杯 500ml','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/b237b8e5-21fe-4edb-b6bc-15757c2acb7c.jpg','醫療級不鏽鋼，長效保溫保冰，簡約外型，可裝咖啡茶飲。',450,1,'2025-12-23 13:10:47','2025-12-23 13:14:17',5,45),(28,'原子習慣 (暢銷經典)','https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&q=80&w=800','細微改變帶來巨大成就的實證法則，建立好習慣的必讀之作。',380,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',6,50),(29,'哈利波特：全集典藏版','https://emp-mall-image-2025.s3.ap-northeast-1.amazonaws.com/9ae56929-ab53-4353-9716-e7c6a0a5e845.png','奇幻文學經典，精裝盒裝版，值得永久珍藏的魔法世界。',2500,1,'2025-12-23 13:10:47','2025-12-23 13:13:30',6,10),(30,'國家地理雜誌 一年期訂閱','https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=800','探索世界奧秘，每期精美攝影與深度報導，每月寄送到府。',1800,1,'2025-12-23 13:10:47','2025-12-23 13:10:47',6,19),(31,'羅技 MX Master 3S 無線滑鼠','https://resource.logitech.com/w_692,c_lpad,ar_4:3,q_auto:best,f_auto,dpr_auto/content/dam/logitech/en/products/mice/mx-master-3s/gallery/mx-master-3s-mouse-top-view-graphite.png','人體工學設計，安靜電磁滾輪，Type-C 快充，提升工作效率的首選。',3500,1,'2025-12-23 14:17:37','2025-12-23 15:33:35',1,20);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-24 14:11:15
