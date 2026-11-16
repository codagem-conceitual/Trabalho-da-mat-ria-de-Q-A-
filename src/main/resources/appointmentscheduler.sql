CREATE TABLE IF NOT EXISTS `roles` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`name` varchar(50) DEFAULT NULL,
	PRIMARY KEY (`id`)
)
	ENGINE=InnoDB
	AUTO_INCREMENT=1
	DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `users` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`username` varchar(50) NOT NULL,
	`password` char(80) NOT NULL,
	`first_name` varchar(50),
	`last_name` varchar(50),
	`email` varchar(50),
	`mobile` varchar(50),
	`street` varchar(50),
	`city` varchar(50),
	`postcode` varchar(50),
  PRIMARY KEY (`id`)
)
	ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `users_roles` (
	`user_id` int(11) NOT NULL,
	`role_id` int(11) NOT NULL,
	PRIMARY KEY (`user_id`,`role_id`),
	KEY `FK_ROLE_idx` (`role_id`),

	CONSTRAINT `FK_users_user` FOREIGN KEY (`user_id`)
	REFERENCES `users` (`id`)
	ON DELETE NO ACTION ON UPDATE NO ACTION,

	CONSTRAINT `FK_roles_role` FOREIGN KEY (`role_id`)
	REFERENCES `roles` (`id`)
	ON DELETE NO ACTION ON UPDATE NO ACTION
)
	ENGINE=InnoDB
  DEFAULT CHARSET=utf8;
	SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE IF NOT EXISTS `works` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256),
  `duration` INT(11),
  `price` DECIMAL(10, 2),
	`editable` BOOLEAN,
	`target` VARCHAR(256),
  `description` TEXT,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `invoices` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`number` VARCHAR(256),
	`status` VARCHAR(256),
	`total_amount` DECIMAL(10, 2),
	`issued` DATETIME,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `appointments` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `start` DATETIME,
  `end` DATETIME,
	`canceled_at` DATETIME,
	`status` VARCHAR(20),
	`id_canceler` INT(11),
  `id_provider` INT(11),
  `id_customer` INT(11),
  `id_work` INT(11),
	`id_invoice` INT(11),
  PRIMARY KEY (`id`),
	KEY `id_canceler` (`id_canceler`),
  KEY `id_provider` (`id_provider`),
  KEY `id_customer` (`id_customer`),
  KEY `id_work` (`id_work`),
	KEY `id_invoice` (`id_invoice`),
	CONSTRAINT `appointments_users_canceler` FOREIGN KEY (`id_canceler`) REFERENCES `users` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  CONSTRAINT `appointments_users_customer` FOREIGN KEY (`id_customer`) REFERENCES `users` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  CONSTRAINT `appointments_works` FOREIGN KEY (`id_work`) REFERENCES `works` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  CONSTRAINT `appointments_users_provider` FOREIGN KEY (`id_provider`) REFERENCES `users` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
	CONSTRAINT `appointments_invoices` FOREIGN KEY (`id_invoice`) REFERENCES `invoices` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE

)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE IF NOT EXISTS `works_providers` (
  `id_user` INT(11) NOT NULL,
  `id_work` INT(11) NOT NULL,
  PRIMARY KEY (`id_user`, `id_work`),
  KEY `idx_works_providers_work` (`id_work`),
  CONSTRAINT `works_providers_users_provider` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
  CONSTRAINT `works_providers_works` FOREIGN KEY (`id_work`) REFERENCES `works` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `working_plans` (
	`id_provider` int(11) NOT NULL,
  `monday` TEXT,
	`tuesday` TEXT,
	`wednesday` TEXT,
	`thursday` TEXT,
	`friday` TEXT,
	`saturday` TEXT,
	`sunday` TEXT,

  PRIMARY KEY (`id_provider`),
	KEY `idx_working_plans_provider` (`id_provider`),

	CONSTRAINT `FK_appointments_provider` FOREIGN KEY (`id_provider`)
	REFERENCES `users` (`id`)

	ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `messages` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`created_at` DATETIME,
	`message` TEXT,
	`id_author` INT(11),
  `id_appointment` INT(11),
  PRIMARY KEY (`id`),
	KEY `id_author` (`id_author`),
	KEY `id_appointment` (`id_appointment`),

	CONSTRAINT `FK_notes_author` FOREIGN KEY (`id_author`)
  REFERENCES `users` (`id`)
	ON DELETE NO ACTION
  ON UPDATE NO ACTION,

	CONSTRAINT `FK_notes_appointment` FOREIGN KEY (`id_appointment`)
	REFERENCES `appointments` (`id`)
	ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



CREATE TABLE IF NOT EXISTS `corporate_customers` (
	`id_customer` int(11) NOT NULL,
	`vat_number` VARCHAR(256),
	`company_name` VARCHAR(256),
  PRIMARY KEY (`id_customer`),
	KEY `idx_corporate_customers_customer` (`id_customer`),
	CONSTRAINT `FK_corporate_customer_user` FOREIGN KEY (`id_customer`)
	REFERENCES `users` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `providers` (
	`id_provider` int(11) NOT NULL,
  PRIMARY KEY (`id_provider`),
	KEY `idx_providers_provider` (`id_provider`),
	CONSTRAINT `FK_provider_user` FOREIGN KEY (`id_provider`)
	REFERENCES `users` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS  `retail_customers` (
	`id_customer` int(11) NOT NULL,
  PRIMARY KEY (`id_customer`),
	KEY `idx_retail_customers_customer` (`id_customer`),
	CONSTRAINT `FK_retail_customer_user` FOREIGN KEY (`id_customer`)
	REFERENCES `users` (`id`)
)
	ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `customers` (
	`id_customer` int(11) NOT NULL,
  PRIMARY KEY (`id_customer`),
	KEY `idx_customers_customer` (`id_customer`),

	CONSTRAINT `FK_customer_user` FOREIGN KEY (`id_customer`)
	REFERENCES `users` (`id`)
)
	ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `notifications` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`title` VARCHAR(256),
	`message` TEXT,
  `created_at` DATETIME,
  `url` VARCHAR(256),
	`is_read` BOOLEAN,
  `id_user` INT(11),
  PRIMARY KEY (`id`),
	KEY `id_user` (`id_user`),

	CONSTRAINT `FK_notification_user` FOREIGN KEY (`id_user`)
  REFERENCES `users` (`id`)
	ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


CREATE TABLE IF NOT EXISTS `exchanges` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`exchange_status` VARCHAR(256),
  `id_appointment_requestor` INT(11),
  `id_appointment_requested` INT(11),
  PRIMARY KEY (`id`),
	KEY `id_appointment_requestor` (`id_appointment_requestor`),
KEY `id_appointment_requested` (`id_appointment_requested`),
	CONSTRAINT `FK_exchange_appointment_requestor` FOREIGN KEY (`id_appointment_requestor`)
  REFERENCES `appointments` (`id`)
	ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  	CONSTRAINT `FK_exchange_appointment_requested` FOREIGN KEY (`id_appointment_requested`)
  REFERENCES `appointments` (`id`)
	ON DELETE NO ACTION
  ON UPDATE NO ACTION
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- Perfis de acesso disponíveis
INSERT INTO `roles` (id,name) VALUES
  (1,'ROLE_ADMIN'),
  (2,'ROLE_PROVIDER'),
  (3,'ROLE_CUSTOMER'),
  (4,'ROLE_CUSTOMER_CORPORATE'),
  (5,'ROLE_CUSTOMER_RETAIL');

-- Dados de exemplo do ClínicaFácil (pessoas fictícias).
-- Todos os usuários de exemplo usam a senha 'qwerty123'.

-- Coordenação da clínica-escola (administrador): usuário 'coordenacao'
INSERT INTO `users` (id, username, password, first_name, last_name, email)
VALUES (1, 'coordenacao', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi',
        'Coordenação', 'Clínica-Escola', 'coordenacao@clinicafacil.example');
INSERT INTO `users_roles` (user_id, role_id)
VALUES (1, 1);

-- Estagiário de Psicologia e Nutrição: usuário 'estagiario'
INSERT INTO `users` (id, username, password, first_name, last_name, email)
VALUES (2, 'estagiario', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi',
        'Lucas', 'Pereira', 'lucas.estagiario@clinicafacil.example');
INSERT INTO `providers` (id_provider)
VALUES (2);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (2, 2);

-- Paciente da comunidade: usuário 'paciente'
INSERT INTO `users` (id, username, password, first_name, last_name, email, mobile)
VALUES (3, 'paciente', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi',
        'Maria', 'Souza', 'maria.paciente@clinicafacil.example', '84999990000');
INSERT INTO `customers` (id_customer)
VALUES (3);
INSERT INTO `retail_customers` (id_customer)
VALUES (3);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (3, 3);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (3, 5);

-- Estagiária de Fisioterapia e Odontologia: usuário 'estagiaria'
INSERT INTO `users` (id, username, password, first_name, last_name, email)
VALUES (4, 'estagiaria', '$2a$10$EqKcp1WFKVQISheBxkQJoOqFbsWDzGJXRz/tjkGq85IZKJJ1IipYi',
        'Ana', 'Costa', 'ana.estagiaria@clinicafacil.example');
INSERT INTO `providers` (id_provider)
VALUES (4);
INSERT INTO `users_roles` (user_id, role_id)
VALUES (4, 2);

-- Especialidades da clínica-escola (atendimento gratuito)
INSERT INTO `works` (id, name, duration, price, editable, target, description)
VALUES (1, 'Psicologia', 50, 0.00, true, 'retail',
        'Atendimento psicológico com estagiário supervisionado. Duração de 50 minutos.'),
       (2, 'Fisioterapia', 50, 0.00, true, 'retail',
        'Sessão de fisioterapia com estagiário supervisionado. Duração de 50 minutos.'),
       (3, 'Nutrição', 40, 0.00, true, 'retail',
        'Consulta de nutrição com estagiário supervisionado. Duração de 40 minutos.'),
       (4, 'Odontologia', 60, 0.00, true, 'retail',
        'Atendimento odontológico com estagiário supervisionado. Duração de 60 minutos.');

INSERT INTO works_providers
VALUES (2, 1),
       (2, 3),
       (4, 2),
       (4, 4);

-- Horário da clínica: segunda a sexta, 8h às 17h, com almoço das 12h às 13h.
-- Sábado e domingo fechado (início igual ao fim = nenhum horário livre).
INSERT INTO working_plans
VALUES (2,
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[8,0]},"breaks":[]}',
        '{"workingHours":{"start":[8,0],"end":[8,0]},"breaks":[]}'),
       (4,
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[17,0]},"breaks":[{"start":[12,0],"end":[13,0]}]}',
        '{"workingHours":{"start":[8,0],"end":[8,0]},"breaks":[]}',
        '{"workingHours":{"start":[8,0],"end":[8,0]},"breaks":[]}');

