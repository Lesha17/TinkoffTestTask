-- Judging a database structure in the task description, Oracle database
-- is used in the team I am applying to and preferable.
-- I have only postgre installed on my Mac and don't want to spend my time installing
-- another DB.
-- As Oracle fully supports postgre API, this script would work.
CREATE TABLE IF NOT EXISTS public.FIND_NUMBER_RESULTS
(
    ID SERIAL PRIMARY KEY,
    CODE VARCHAR(50) NOT NULL,
    NUMBER INTEGER NOT NULL,
    FILENAMES VARCHAR(100),
    ERROR VARCHAR(100)
)