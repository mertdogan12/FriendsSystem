CREATE TABLE IF NOT EXISTS `friends`.`friends`
(
    `player1` UUID NOT NULL ,
    `player2` UUID NOT NULL,
    UNIQUE (`player1`, `player2`)
)
ENGINE = InnoDB;