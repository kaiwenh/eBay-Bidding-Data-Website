CREATE TABLE Location (ItemID INT NOT NULL, Position GEOMETRY NOT NULL, PRIMARY KEY(ItemID), SPATIAL INDEX(Position)) ENGINE=MyISAM;

INSERT INTO Location (ItemID, Position) 
SELECT ItemID, Point(Latitude, Longitude) FROM Items WHERE Latitude IS NOT NULL AND Longitude IS NOT NULL;