INSERT INTO `role` (`role`) VALUES ('ADMIN');
INSERT INTO `role` (`role`) VALUES ('MANAGER');
INSERT INTO `role` (`role`) VALUES ('USER');
INSERT INTO `user` (`active`, `email`, `full_name`, `join_date`, `last_login_date`, `non_locked`, `password`, `updated_at`, `username`) VALUES (CONV('1', 2, 10) + 0, 'admin@example.com', 'Administrator', now(), NULL, CONV('1', 2, 10) + 0, '$2a$10$8L.jmdP9sgoJWx7TRLS26ulrXVy1i.q3Y3ZW6TzzTOSG.fdh93k3i', NULL, 'admin');
INSERT INTO `user_roles` (`users_id`, `roles_id`) VALUES ('1', '1');
