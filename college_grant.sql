-- 创建数据库
CREATE DATABASE IF NOT EXISTS college_grant 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

USE college_grant;

-- 1. 用户登录表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录账号',
    password    VARCHAR(100) NOT NULL COMMENT '密码（MD5）',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0管理员，1教师，2学生',
    user_id     BIGINT       COMMENT '关联ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录表';

-- 2. 学生信息表
DROP TABLE IF EXISTS student;
CREATE TABLE student (
    id                  BIGINT        PRIMARY KEY AUTO_INCREMENT,
    student_no          VARCHAR(20)   NOT NULL UNIQUE COMMENT '学号',
    name                VARCHAR(50)   NOT NULL COMMENT '姓名',
    gender              CHAR(1)       DEFAULT '0' COMMENT '性别：0男，1女',
    college             VARCHAR(100) COMMENT '学院',
    major               VARCHAR(100) COMMENT '专业',
    class_name          VARCHAR(50)  COMMENT '班级',
    phone               VARCHAR(20)  COMMENT '联系电话',
    family_income       DECIMAL(10,2) COMMENT '家庭年收入',
    family_members      INT           DEFAULT 1 COMMENT '家庭人口数',
    per_capita_income   DECIMAL(10,2) COMMENT '人均年收入',
    special_type        TINYINT       DEFAULT 0 COMMENT '特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾',
    create_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 3. 教师信息表
DROP TABLE IF EXISTS teacher;
CREATE TABLE teacher (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    teacher_no  VARCHAR(20)  NOT NULL UNIQUE COMMENT '工号',
    name        VARCHAR(50)  NOT NULL COMMENT '姓名',
    college     VARCHAR(100) COMMENT '学院',
    phone       VARCHAR(20)  COMMENT '联系电话',
    title       TINYINT      DEFAULT 0 COMMENT '职称：0助教，1讲师，2副教授，3教授',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 4. 助学金类型表
DROP TABLE IF EXISTS grant_type;
CREATE TABLE grant_type (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    type_name   VARCHAR(100)  NOT NULL COMMENT '类型名称',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态：0停用，1可申请',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='助学金类型表';

-- 5. 助学金申请表
DROP TABLE IF EXISTS grant_application;
CREATE TABLE grant_application (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    student_id      BIGINT       NOT NULL COMMENT '学生ID',
    type_id         BIGINT       NOT NULL COMMENT '助学金类型ID',
    apply_reason    TEXT         NOT NULL COMMENT '申请理由',
    family_situation TEXT        COMMENT '家庭情况描述',
    special_type    TINYINT      DEFAULT 0 COMMENT '特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾',
    special_proof   VARCHAR(255) DEFAULT NULL COMMENT '特殊困难证明材料路径',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0待审核，1通过，2驳回，3已评分，4已评定，5已公示',
    reviewer_id     BIGINT       COMMENT '审核教师ID',
    review_comment  VARCHAR(500) COMMENT '审核意见',
    review_time     DATETIME     COMMENT '审核时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='助学金申请表';

-- 6. 评分记录表
DROP TABLE IF EXISTS score_record;
CREATE TABLE score_record (
    id                      BIGINT        PRIMARY KEY AUTO_INCREMENT,
    application_id          BIGINT        NOT NULL COMMENT '申请ID',
    economic_score          DECIMAL(5,2)  NOT NULL COMMENT '家庭经济情况分(0-100)，收入越低分越高',
    family_burden_score     DECIMAL(5,2)  NOT NULL COMMENT '家庭负担情况分(0-100)，负担越重分越高',
    special_hardship_score  DECIMAL(5,2)  NOT NULL COMMENT '特殊困难程度分(0-100)，困难程度越高分越高',
    disaster_score          DECIMAL(5,2)  NOT NULL COMMENT '突发困难情况分(0-100)，受灾/重大疾病等',
    scorer_id               BIGINT        NOT NULL COMMENT '评分教师ID',
    score_comment           VARCHAR(500)  DEFAULT NULL COMMENT '评分评语',
    score_time              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间',
    create_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_application (application_id),
    INDEX idx_scorer (scorer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分记录表';

-- 7. 助学金等级表
DROP TABLE IF EXISTS grant_level;
CREATE TABLE grant_level (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    type_id     BIGINT        NOT NULL COMMENT '助学金类型ID',
    level_name  VARCHAR(50)   NOT NULL COMMENT '等级名称',
    amount      DECIMAL(10,2) NOT NULL COMMENT '资助金额',
    min_score   DECIMAL(5,2)  COMMENT '最低分数线',
    sort        INT           NOT NULL DEFAULT 1 COMMENT '排序（1最高）',
    level_count INT           NOT NULL DEFAULT 3 COMMENT '等级数量：1非分级，3分级',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type_id (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='助学金等级表';

-- 8. 评定结果表
DROP TABLE IF EXISTS evaluation_result;
CREATE TABLE evaluation_result (
    id              BIGINT        PRIMARY KEY AUTO_INCREMENT,
    application_id  BIGINT        NOT NULL COMMENT '申请ID',
    student_id      BIGINT        NOT NULL COMMENT '学生ID',
    total_score     DECIMAL(5,2)  NOT NULL COMMENT 'AHP加权总分',
    score_rank      INT           NOT NULL COMMENT '排名',
    level_id        BIGINT        NOT NULL COMMENT '关联grant_level表ID',
    evaluator_id    BIGINT        NOT NULL COMMENT '评定人ID',
    evaluate_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_application (application_id),
    INDEX idx_rank (score_rank),
    INDEX idx_level_id (level_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评定结果表';

-- 9. 公示表
DROP TABLE IF EXISTS public_notice;
CREATE TABLE public_notice (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(200) NOT NULL COMMENT '公示标题',
    content      TEXT         COMMENT '公示内容',
    type_id      BIGINT       NOT NULL COMMENT '助学金类型ID',
    start_time   DATETIME     COMMENT '公示开始时间',
    end_time     DATETIME     COMMENT '公示结束时间',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0未发布，1公示中，2已结束',
    publisher_id BIGINT       COMMENT '发布人ID',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_type_id (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公示表';

-- ==================== 初始数据 ====================

-- 管理员账号（密码：123456的MD5）
INSERT INTO sys_user (username, password, role, user_id, status) 
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 0, NULL, 1);

-- 助学金类型
INSERT INTO grant_type (type_name, status, description) VALUES
('国家助学金', 1, '面向家庭经济困难学生'),
('国家励志奖学金', 1, '面向品学兼优的家庭经济困难学生'),
('校级助学金', 1, '学校自设助学金');

-- 助学金等级
INSERT INTO grant_level (type_id, level_name, amount, min_score, sort, level_count) VALUES
(1, '一等', 4400.00, 85.00, 1, 3),
(1, '二等', 3300.00, 70.00, 2, 3),
(1, '三等', 2200.00, 60.00, 3, 3),
(2, '获奖', 5000.00, 80.00, 1, 1),
(3, '一等', 3000.00, 80.00, 1, 2),
(3, '二等', 2000.00, 65.00, 2, 2);
