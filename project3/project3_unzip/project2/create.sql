CREATE TABLE Items (ItemID INT, Name VARCHAR(100), Currently DECIMAL(8,2), Buy_Price DECIMAL(8,2), First_Bid DECIMAL(8,2), Number_of_Bids INT, Latitude DOUBLE, Longitude DOUBLE, Location VARCHAR(100), Country VARCHAR(40), Started TIMESTAMP, Ends TIMESTAMP, Seller_UserID VARCHAR(40), Description VARCHAR(4000), PRIMARY KEY(ItemID));
CREATE TABLE Category (ItemID INT, Title VARCHAR(40), PRIMARY KEY(ItemID, Title));
CREATE TABLE Bids (Bidder_UserID VARCHAR(40), Time TIMESTAMP, ItemID INT, Amount DECIMAL(8,2), PRIMARY KEY(Bidder_UserID, Time));
CREATE TABLE Bidder (UserID VARCHAR(40), Rating INT, Latitude DOUBLE, Longitude DOUBLE, Location VARCHAR(100), Country VARCHAR(40), PRIMARY KEY(UserID));
CREATE TABLE Seller (UserID VARCHAR(40), Rating INT, PRIMARY KEY(UserID));