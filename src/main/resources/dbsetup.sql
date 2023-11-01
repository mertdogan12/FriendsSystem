CREATE TABLE IF NOT EXISTS `friends`
(
    `player1` UUID NOT NULL,
    `player2` UUID NOT NULL,
    `timestamp` DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (`player1`, `player2`)
)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `requests`
(
    `sender` UUID NOT NULL,
    `receiver` UUID NOT NULL,
    `timestamp` DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (`sender`, `receiver`)
)
ENGINE = InnoDB;
