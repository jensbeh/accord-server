CREATE TABLE IF NOT EXISTS `accord_db`.`user` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `online` BIT(1) NOT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `user_key` VARCHAR(36) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `accord_db`.`server` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  `owner` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `accord_db`.`user_has_server` (
  `user_id` INT NOT NULL,
  `server_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `server_id`),
  INDEX `fk_user_has_server_server1_idx` (`server_id` ASC) VISIBLE,
  INDEX `fk_user_has_server_user_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_user_has_server_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `accord_db`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_server_server1`
    FOREIGN KEY (`server_id`)
    REFERENCES `accord_db`.`server` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `accord_db`.`categories` (
  `id` INT NOT NULL,
  `name` VARCHAR(255) NULL,
  `server_id` INT NOT NULL,
  PRIMARY KEY (`id`, `server_id`),
  INDEX `fk_Categories_server1_idx` (`server_id` ASC) VISIBLE,
  CONSTRAINT `fk_Categories_server1`
    FOREIGN KEY (`server_id`)
    REFERENCES `accord_db`.`server` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `accord_db`.`channels` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  `categories_id` INT NOT NULL,
  `categories_server_id` INT NOT NULL,
  `type` VARCHAR(45) NULL,
  `privileged` BIT(1) NULL,
  PRIMARY KEY (`id`, `categories_id`, `categories_server_id`),
  INDEX `fk_Channel_Categories1_idx` (`categories_id` ASC, `categories_server_id` ASC) VISIBLE,
  CONSTRAINT `fk_Channel_Categories1`
    FOREIGN KEY (`categories_id` , `categories_server_id`)
    REFERENCES `accord_db`.`categories` (`id` , `server_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `accord_db`.`messages` (
  `id` INT NOT NULL,
  `text` VARCHAR(255) NULL,
  `channel_id` INT NOT NULL,
  `channel_categories_id` INT NOT NULL,
  `channel_categories_server_id` INT NOT NULL,
  `from` VARCHAR(255) NULL,
  `timestamp` VARCHAR(45) NULL,
  PRIMARY KEY (`id`, `channel_id`, `channel_categories_id`, `channel_categories_server_id`),
  INDEX `fk_messages_Channel1_idx` (`channel_id` ASC, `channel_categories_id` ASC, `channel_categories_server_id` ASC) VISIBLE,
  CONSTRAINT `fk_messages_Channel1`
    FOREIGN KEY (`channel_id` , `channel_categories_id` , `channel_categories_server_id`)
    REFERENCES `accord_db`.`channels` (`id` , `categories_id` , `categories_server_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;