DELETE FROM users;
DELETE FROM organizations;
INSERT INTO public.organizations(
	id, action_area, description, name)
	VALUES
	(1,	0,	'Action A',	'AREA_A'),
	(2,	1,	'Action B',	'AREA_B');

INSERT INTO public.users(
	id, password, registration_date, user_role, username, organization_id)
	VALUES
	(202,	'{bcrypt}$2a$10$Trb..7ueUPa/EAPsQlBfKesPo22E12BWwuPlooeBP7bnezZRjcnsy',	1,	'SUPER_ADMIN',	'superAdmin', null),
	(203,	'{bcrypt}$2a$10$Trb..7ueUPa/EAPsQlBfKesPo22E12BWwuPlooeBP7bnezZRjcnsy',	1,	'ADMIN',   'orgAdmin',	1),
	(204,	'{bcrypt}$2a$10$Trb..7ueUPa/EAPsQlBfKesPo22E12BWwuPlooeBP7bnezZRjcnsy',	1,	'MANAGER',   'orgManager',	1),
	(205,	'{bcrypt}$2a$10$Trb..7ueUPa/EAPsQlBfKesPo22E12BWwuPlooeBP7bnezZRjcnsy',	1,	'FIRST_RESPONDENT',	'orgRespondent',	1);
