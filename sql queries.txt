-- ALL QUERIES TO FILL THE POKEMON DATABASE
-- To reproduce, copy paste all contents of the file.


-- CREATE QUERIES

-- Species
create table Species
(Pokedex_ID int not null,
Name nvarchar(50) not null,
Primary_Type nvarchar(20) not null,
Secondary_Type nvarchar(20),
primary key (Pokedex_ID),
check ([Pokedex_ID]>=(1) AND [Pokedex_ID]<=(1015)),
check ([Primary_Type]='Fairy' OR [Primary_Type]='Steel' OR [Primary_Type]='Dragon' OR [Primary_Type]='Dark' OR [Primary_Type]='Ghost' OR [Primary_Type]='Rock' OR [Primary_Type]='Bug' OR [Primary_Type]='Psychic' OR [Primary_Type]='Flying' OR [Primary_Type]='Ground' OR [Primary_Type]='Poison' OR [Primary_Type]='Fighting' OR [Primary_Type]='Ice' OR [Primary_Type]='Electric' OR [Primary_Type]='Grass' OR [Primary_Type]='Water' OR [Primary_Type]='Fire' OR [Primary_Type]='Normal'),
check ([Primary_Type]<>[Secondary_Type] AND ([Secondary_Type]='Fairy' OR [Secondary_Type]='Steel' OR [Secondary_Type]='Dragon' OR [Secondary_Type]='Dark' OR [Secondary_Type]='Ghost' OR [Secondary_Type]='Rock' OR [Secondary_Type]='Bug' OR [Secondary_Type]='Psychic' OR [Secondary_Type]='Flying' OR [Secondary_Type]='Ground' OR [Secondary_Type]='Poison' OR [Secondary_Type]='Fighting' OR [Secondary_Type]='Ice' OR [Secondary_Type]='Electric' OR [Secondary_Type]='Grass' OR [Secondary_Type]='Water' OR [Secondary_Type]='Fire' OR [Secondary_Type]='Normal')))

-- Trainer
create table Trainer
(ID int not null,
Name nvarchar(50) not null,
Age tinyint not null,
Height int not null,
Weight int not null,
primary key (ID))

-- Pokemon
create table Pokemon
(ID int not null,
Pokedex_ID int not null,
Nickname nvarchar(50),
Height int not null,
Weight int not null,
Level tinyint not null,
primary key (ID),
foreign key (Pokedex_ID) references Species(Pokedex_ID),
check ([Level]>(0) AND [Level]<(101)))

-- Evolution
create table Evolution
(Pokedex_ID int not null,
Evolution_ID int not null,
primary key (Pokedex_ID, Evolution_ID),
foreign key (Pokedex_ID) references Species(Pokedex_ID),
foreign key (Evolution_ID) references Species(Pokedex_ID))

-- TrainedBy
create table TrainedBy
(Pokemon_ID int not null,
Trainer_ID int,
primary key (Pokemon_ID),
foreign key (Pokemon_ID) references Pokemon(ID),
foreign key (Trainer_ID) references Trainer(ID) on delete set null)

-- Battle
create table Battle
(ID int not null,
Winning_Trainer_ID int not null,
Losing_Trainer_ID int not null,
primary key (ID),
foreign key (Winning_Trainer_ID) references Trainer(ID),
foreign key (Losing_Trainer_ID) references Trainer(ID))

-- PokemonBattleHistory
create table PokemonBattleHistory
(Battle_ID int not null,
Pokemon_ID int not null,
Trainer_ID int not null,
primary key (Battle_ID, Pokemon_ID),
foreign key (Battle_ID) references Battle(ID),
foreign key (Pokemon_ID) references Pokemon(ID),
foreign key (Trainer_ID) references Trainer(ID))


-- INSERT QUERIES

-- Species:
insert into Species values (25, 'Pikachu', 'Electric', NULL)
insert into Species values (26, 'Raichu', 'Electric', NULL)
insert into Species values (172, 'Pichu', 'Electric', NULL)
insert into Species values (4, 'Charmander', 'Fire', NULL)
insert into Species values (5, 'Charmeleon', 'Fire', NULL)
insert into Species values (6, 'Charizard', 'Fire', 'Flying')
insert into Species values (133, 'Eevee', 'Normal', NULL)
insert into Species values (134, 'Vaporeon', 'Water', NULL)
insert into Species values (135, 'Jolteon', 'Electric', NULL)
insert into Species values (136, 'Flareon', 'Fire', NULL)
insert into Species values (196, 'Espeon', 'Psychic', NULL)
insert into Species values (197, 'Umbreon', 'Dark', NULL)
insert into Species values (470, 'Leafeon', 'Grass', NULL)
insert into Species values (471, 'Glaceon', 'Ice', NULL)
insert into Species values (700, 'Sylveon', 'Fairy', NULL)
insert into Species values (120, 'Staryu', 'Water', NULL)
insert into Species values (121, 'Starmie', 'Water', 'Psychic')
insert into Species values (150, 'Mewtwo', 'Psychic', NULL)
insert into Species values (10, 'Caterpie', 'Bug', NULL)
insert into Species values (11, 'Metapod', 'Bug', NULL)
insert into Species values (12, 'Butterfree', 'Bug', 'Flying')
insert into Species values (420, 'Cherubi', 'Grass', NULL)
insert into Species values (421, 'Cherrim', 'Grass', NULL)
insert into Species values (1, 'Bulbasaur', 'Grass', 'Poison')
insert into Species values (2, 'Ivysaur', 'Grass', 'Poison')
insert into Species values (3, 'Venusaur', 'Grass', 'Poison')
insert into Species values (7, 'Squirtle', 'Water', NULL)
insert into Species values (8, 'Wartortle', 'Water', NULL)
insert into Species values (9, 'Blastoise', 'Water', NULL)
insert into Species values (236, 'Tyrogue', 'Fighting', NULL)
insert into Species values (106, 'Hitmonlee', 'Fighting', NULL)
insert into Species values (107, 'Hitmonchan', 'Fighting', NULL)
insert into Species values (237, 'Hitmontop', 'Fighting', NULL)
insert into Species values (74, 'Geodude', 'Rock', 'Ground')
insert into Species values (75, 'Graveler', 'Rock', 'Ground')
insert into Species values (76, 'Golem', 'Rock', 'Ground')
insert into Species values (92, 'Gastly', 'Ghost', 'Poison')
insert into Species values (93, 'Haunter', 'Ghost', 'Poison')
insert into Species values (94, 'Gengar', 'Ghost', 'Poison')
insert into Species values (147, 'Dratini', 'Dragon', NULL)
insert into Species values (148, 'Dragonair', 'Dragon', NULL)
insert into Species values (149, 'Dragonite', 'Dragon', 'Flying')
insert into Species values (215, 'Sneasel', 'Dark', 'Ice')
insert into Species values (461, 'Weavile', 'Dark', 'Ice')
insert into Species values (214, 'Heracross', 'Bug', 'Fighting')
insert into Species values (707, 'Klefki', 'Steel', 'Fairy')
insert into Species values (494, 'Victini', 'Psychic', 'Fire')
insert into Species values (728, 'Popplio', 'Water', NULL)
insert into Species values (729, 'Brionne', 'Water', NULL)
insert into Species values (730, 'Primarina', 'Water', 'Fairy')
insert into Species values (223, 'Remoraid', 'Water', NULL)
insert into Species values (224, 'Octillery', 'Water', NULL)
insert into Species values (349, 'Feebas', 'Water', NULL)
insert into Species values (350, 'Milotic', 'Water', NULL)
insert into Species values (353, 'Shuppet', 'Ghost', NULL)
insert into Species values (354, 'Banette', 'Ghost', NULL)

-- Evolution:
insert into Evolution values (25, 26)
insert into Evolution values (172, 25)
insert into Evolution values (4, 5)
insert into Evolution values (5, 6)
insert into Evolution values (133, 134)
insert into Evolution values (133, 135)
insert into Evolution values (133, 136)
insert into Evolution values (133, 196)
insert into Evolution values (133, 197)
insert into Evolution values (133, 470)
insert into Evolution values (133, 471)
insert into Evolution values (133, 700)
insert into Evolution values (120, 121)
insert into Evolution values (10, 11)
insert into Evolution values (11, 12)
insert into Evolution values (420, 421)
insert into Evolution values (1, 2)
insert into Evolution values (2, 3)
insert into Evolution values (7, 8)
insert into Evolution values (8, 9)
insert into Evolution values (236, 106)
insert into Evolution values (236, 107)
insert into Evolution values (236, 237)
insert into Evolution values (74, 75)
insert into Evolution values (75, 76)
insert into Evolution values (92, 93)
insert into Evolution values (93, 94)
insert into Evolution values (215, 461)
insert into Evolution values (147, 148)
insert into Evolution values (148, 149)
insert into Evolution values (728, 729)
insert into Evolution values (729, 730)
insert into Evolution values (223, 224)
insert into Evolution values (349, 350)
insert into Evolution values (353, 354)

-- Trainer:
insert into Trainer values (1, 'Ash', 10, 55, 105)
insert into Trainer values (2, 'Misty', 12, 57, 87)
insert into Trainer values (3, 'James', 20, 75, 165)
insert into Trainer values (4, 'Victor', 21, 74, 140)
insert into Trainer values (5, 'Erik', 21, 72, 175)

-- Pokemon:
insert into Pokemon values (1, 7, 'Squirt', 16, 13, 100)
insert into Pokemon values (2, 121, 'Nebula', 43, 176, 25)
insert into Pokemon values (3, 8, 'Cannon Boy', 63, 189, 47)
insert into Pokemon values (4, 134, 'Mermaid', 39, 64, 45)
insert into Pokemon values (5, 728, 'Pooplio', 16, 17, 41)
insert into Pokemon values (6, 94, 'Shadow', 59, 89, 68)
insert into Pokemon values (7, 6, 'Rawrizard', 67, 200, 73)
insert into Pokemon values (8, 494, 'Savage', 16, 9, 96)
insert into Pokemon values (9, 149, 'Mailman', 84, 463, 50)
insert into Pokemon values (10, 150, 'Chad', 79, 269, 75)
insert into Pokemon values (11, 223, 'Remoraid', 100, 100, 100)
insert into Pokemon values (12, 224, 'Octillery', 100, 100, 100)
insert into Pokemon values (13, 349, 'Feebas', 100, 100, 100)
insert into Pokemon values (14, 350, 'Milotic', 100, 100, 100)
insert into Pokemon values (15, 353, 'Shuppet', 100, 100, 100)
insert into Pokemon values (16, 354, 'Banette', 100, 100, 100)

-- TrainedBy:
insert into TrainedBy values (1, 1)
insert into TrainedBy values (2, 2)
insert into TrainedBy values (3, 2)
insert into TrainedBy values (4, 2)
insert into TrainedBy values (5, 2)
insert into TrainedBy values (6, 3)
insert into TrainedBy values (7, 3)
insert into TrainedBy values (8, 4)
insert into TrainedBy values (9, 4)
insert into TrainedBy values (10, 4)

-- Battle:
insert into Battle values (1, 2, 1)
insert into Battle values (2, 4, 3)
insert into Battle values (3, 3, 1)
insert into Battle values (4, 4, 2)
insert into Battle values (5, 4, 1)

-- PokemonBattleHistory
insert into PokemonBattleHistory values (1, 1, 1)
insert into PokemonBattleHistory values (1, 2, 2)
insert into PokemonBattleHistory values (1, 3, 2)
insert into PokemonBattleHistory values (1, 4, 2)
insert into PokemonBattleHistory values (1, 5, 2)
insert into PokemonBattleHistory values (2, 8, 4)
insert into PokemonBattleHistory values (2, 9, 4)
insert into PokemonBattleHistory values (2, 10, 4)
insert into PokemonBattleHistory values (2, 6, 3)
insert into PokemonBattleHistory values (2, 7, 3)
insert into PokemonBattleHistory values (3, 6, 3)
insert into PokemonBattleHistory values (3, 7, 3)
insert into PokemonBattleHistory values (3, 1, 1)
insert into PokemonBattleHistory values (4, 2, 2)
insert into PokemonBattleHistory values (4, 8, 4)
insert into PokemonBattleHistory values (5, 1, 1)
insert into PokemonBattleHistory values (5, 8, 4)