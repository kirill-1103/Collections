-- insert into usr (id, email, login, name, password, status)
--     values (1002,'k@mail.ru','kirill','Кирилл','$2a$08$rgs7YExtY7XwHpuJE7aHjOtXmfJXIMceNwYXB7bRtIlYABkCzy89O',true);
--
-- insert into user_role (user_id, roles)
--     values(1002,'USER');
--
-- insert into usr (id, email, login, name, password, status)
-- values (2002,'admin@mail.ru','admin','Администратор','$2a$08$rgs7YExtY7XwHpuJE7aHjOtXmfJXIMceNwYXB7bRtIlYABkCzy89O',true);
--
-- insert into user_role (user_id, roles)
-- values(2002,'ADMIN');

-- delete from user_role where user_id=1002;
-- delete from user_role where user_id=2002;
--
-- delete from usr where id = 1002;
-- delete from usr where id = 2002;