CREATE TABLE players (
     player_id int8 PRIMARY KEY,
     nickname varchar(40) unique
);

CREATE TABLE items (
    id int8 PRIMARY KEY unique not null,
    resource_id int8,
    count int4,
    level int4,
    check ( count >= 0 and level >= 0 )
);


CREATE TABLE progresses(
    id int8 primary key unique not null,
    player_id int8 references players(player_id),
    resource_id int8,
    score int4,
    maxScore int4,
);

CREATE TABLE currencies(
    id int8 primary key unique not null,
    resource_id int8,
    name varchar(100),
    count int4,
    check ( count >= 0 )
);

CREATE TABLE players_currencies(
    player_id int8 references players(player_id),
    currency_id int8 references currencies(id)
);

CREATE TABLE players_items(
    player_id int8 references players(player_id),
    item_id int8 references items(id)
)







