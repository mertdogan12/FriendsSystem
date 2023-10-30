CREATE TABLE IF NOT EXISTS `friends`
(
    `player1` UUID NOT NULL,
    `player2` UUID NOT NULL,
    UNIQUE (`player1`, `player2`)
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `requests`
(
    `sender` UUID NOT NULL,
    `receiver` UUID NOT NULL,
    UNIQUE (`sender`, `receiver`)
)
ENGINE = InnoDB;
