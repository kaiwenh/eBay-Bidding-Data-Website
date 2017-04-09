
-- Query the number of users in the database:

SELECT COUNT(*)
FROM (
SELECT UserID FROM Bidder
UNION
SELECT UserID FROM Seller
) AS A;

-- Query the number of items in "New York":
SELECT COUNT(*)
FROM Items
WHERE BINARY Location = 'New York';

-- Query the number of auctions belonging to exactly four categories:
SELECT COUNT(*)
FROM (
SELECT ItemID
FROM Category
GROUP BY ItemID
HAVING COUNT(Title) = 4
) AS A;

-- Query the ID(s) of current (unsold) auction(s) with the highest bid:
SELECT Items.ItemID
FROM Bids, Items
WHERE Bids.ItemID = Items.ItemID AND Items.Ends >'2001-12-20 00:00:01' AND Bids.Amount = (
SELECT MAX(Bids.Amount)
FROM Bids, Items
WHERE Bids.ItemID = Items.ItemID AND Items.Ends >'2001-12-20 00:00:01'
);

-- Query the number of sellers whose rating is higher than 1000:
SELECT COUNT(*)
FROM Seller
WHERE Rating > 1000;

-- Query the number of users who are both sellers and bidders:
SELECT COUNT(*)
FROM Seller, Bidder
WHERE Seller.UserID = Bidder.UserID;

-- Query the number of categories that include at least one item with a bid of more than $100:
SELECT COUNT(DISTINCT Title)
FROM Bids, Category
WHERE Category.ItemID = Bids.ItemID AND Bids.Amount > 100;

