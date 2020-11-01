BEGIN;
-- password is 'admin'
INSERT INTO "USER" (id, name, pwd, email) VALUES
 (1, 'admin', '$2a$10$M/QuIW0TWmNwooXWtTcdqu2DFez9mcAK0ERgmRyTJdPwS7siom8ya', 'admin@abv.bg');

COMMIT;