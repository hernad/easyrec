
-- -----------------------------------------------------------------------------
-- ------------------------------------ TABLE ITEMASSOC ------------------------
-- -----------------------------------------------------------------------------


-- Show assoc value distribiution (all assoc values)
SELECT
	assocValue, count(1)
FROM
        itemassoc
WHERE
	tenantid = 1
GROUP BY
	assocValue;

-- Show assoc value distribiution (grouped)
SELECT @total := (SELECT count(1) FROM itemassoc WHERE tenantId = 1);
SELECT @tenantid := 1;

SELECT '<20' as assocValue, count(1) as c, (count(1)/@total)*100 FROM itemassoc WHERE tenantid = @tenantid AND assocValue<20 UNION
SELECT '20-40', count(1) as c, (count(1)/@total)*100 FROM itemassoc WHERE tenantid = @tenantid AND assocValue>=20 AND assocValue<40 UNION
SELECT '40-60', count(1) as c, (count(1)/@total)*100 FROM itemassoc WHERE tenantid = @tenantid AND assocValue>=40 AND assocValue<60 UNION
SELECT '60-80', count(1) as c, (count(1)/@total)*100 FROM itemassoc WHERE tenantid = @tenantid AND assocValue>=60 AND assocValue<80 UNION
SELECT '>80', count(1) as c, (count(1)/@total)*100 FROM itemassoc WHERE tenantid = @tenantid AND assocValue>=80;


-- Show assoc value distribiution per item (grouped)
SELECT *
FROM
(SELECT COUNT(g.itemfromid) as group1 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = 1) g) g1 JOIN
(SELECT COUNT(g.itemfromid) as group2 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = 1 AND assocValue >= 10 ) g) g2 JOIN
(SELECT COUNT(g.itemfromid) as group3 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = 1 AND assocValue >= 20 ) g) g3 JOIN
(SELECT COUNT(g.itemfromid) as group4 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = 1 AND assocValue >= 40 ) g) g4 JOIN
(SELECT COUNT(g.itemfromid) as group5 FROM (SELECT distinct itemfromid FROM itemassoc WHERE tenantid = 1 AND assocValue >= 50 ) g) g5


-- -----------------------------------------------------------------------------
-- ------------------------------------ TABLE ACTION ---------------------------
-- -----------------------------------------------------------------------------

-- The following queries operate on the action table.
-- The queries where computed on a Intel Quad Core 2,5Ghz / 4GB RAM / Windows7 64bit / 160GB SSD INTEL160G2GC
-- The test data was created with the Benchmark Utility in
--
-- /test/easyrec/utils/Benchmark.java
--
-- with the following setup:
--
-- NUMBER_OF_TENANTS = 3;
-- NUMBER_OF_USERS   = 500000;
-- NUMBER_OF_ITEMS   = 100000;
-- NUMBER_OF_ACTIONS = 10*1000*1000 * NUMBER_OF_TENANTS; // x actions per tenant


-- Get actions to archive that are older the a given date.
-- 10M entries/tenant: 5 years old date. average computation time 10min
SELECT @tenantid     :=1;
SELECT @cuttoff_date := (SELECT ADDDATE(now(), INTERVAL -5*365 DAY));
SELECT
    Count(1) as c
FROM
    action
WHERE tenantId = @tenantid AND
      actiontime < @cuttoff_date


-- Show user activities during the day group by hours
-- 10M entries/tenant: average computation time 15sec
SELECT @tenantid    :=1;
SELECT
	hour(actiontime), count(1)
FROM
	action
WHERE
	tenantid = @tenantid
GROUP BY
	hour(actiontime);


-- Get assos statisitcs for a given assocType
-- 10M entries/tenant: average computation time 13sec
SELECT @assocTypeId :=1;
SELECT @tenantid    :=1;
SELECT @actionTypeId:=1;

SELECT * FROM
    (SELECT Count(1) as actions FROM action WHERE actionTypeId = @actionTypeId AND tenantid = @tenantid) v JOIN
    (SELECT Count(1) as rules FROM itemassoc WHERE assocTypeId = @assocTypeId AND tenantid = @tenantid) r JOIN
    (SELECT
        ROUND(AVG(c),0) AS averageNumberOfRulesPerItem,
        COUNT(c) AS itemsWithRules,
        ROUND(STD(c),0)   AS stdNumberOfRulesPerItem FROM (
            SELECT itemFromId, count(1) AS c
            FROM itemassoc
            WHERE assocTypeId = @assocTypeId AND tenantid = @tenantid
            GROUP BY itemFromId) b) b



-- get Tenant Statistics
-- 10M entries/tenant: average computation time 4 minutes (!!)
SELECT @tenantid    :=1;

SELECT
    a.actions,
    b.backtracks,
    i.items,
    u.users,
    ROUND(a.actions/u.users,2) AS average_actions_per_user
FROM
    (SELECT count(1) as actions FROM action WHERE tenantid=@tenantid) a JOIN
    (SELECT count(1) as backtracks FROM backtracking WHERE tenantid=@tenantid) b JOIN
    (SELECT count(1) as items FROM (SELECT distinct itemid FROM action where tenantid = @tenantid) a) i JOIN
    (SELECT count(1) as users FROM (SELECT distinct userid FROM action where tenantid = @tenantid) a) u



-- Get Users Statistics for a given Tenant
-- 10M entries/tenant: average computation time 60min (!!)
SELECT @tenantid    :=1;
SELECT
        u1.users_with_1_action,
        u2.users_with_2_actions,
        u5_10.users_with_3_10_actions,
        u10_100.users_with_11_100_actions,
        u100.users_with_101_and_more_actions
FROM
        (select count(1) as users_with_1_action from (select count(1) as actions, userid from action where tenantid = @tenantid group by userid having count(1) = 1 ) u) u1 join
        (select count(1) as users_with_2_actions from (select count(1) as actions, userid from action where tenantid = @tenantid group by userid having count(1) = 2 ) u) u2 join
        (select count(1) as users_with_3_10_actions from (select count(1) as actions, userid from action where tenantid = @tenantid group by userid having count(1) > 2 and count(1) <= 10) u) u5_10 join
        (select count(1) as users_with_11_100_actions from (select count(1) as actions, userid from action where tenantid = @tenantid group by userid having count(1) > 10 and count(1) <= 100) u) u10_100 join
        (select count(1) as users_with_101_and_more_actions from (select count(1) as actions, userid from action where tenantid = @tenantid group by userid having count(1) > 100 ) u) u100

-- Get Users Statistics for a given Tenant from the last X days
-- BETTER Performance: 10M entries/tenant: average computation time 1sec - 1h (depends on X)
SELECT @tenantid   := 1;
SELECT @actiontime := (SELECT INTERVAL -12*31 DAY + NOW());
SELECT
        u1.users_with_1_action,
        u2.users_with_2_actions,
        u5_10.users_with_3_10_actions,
        u10_100.users_with_11_100_actions,
        u100.users_with_101_and_more_actions
FROM
        (select count(1) as users_with_1_action from (select userid from action where tenantid = 1 and actiontypeid= 1 and actiontime > @actiontime group by userid having count(1) = 1) u) u1 join
        (select count(1) as users_with_2_actions from (select userid from action where tenantid = 1 and actiontypeid= 1 and actiontime > @actiontime group by userid having count(1) = 2) u) u2 join
        (select count(1) as users_with_3_10_actions from (select userid from action where tenantid = 1 and actiontypeid= 1 and actiontime > @actiontime group by userid having count(1) > 2 and count(1) <= 10) u) u5_10 join
        (select count(1) as users_with_11_100_actions from (select userid from action where tenantid = 1 and actiontypeid= 1 and actiontime > @actiontime group by userid having count(1) > 10 and count(1) <= 100) u) u10_100 join
        (select count(1) as users_with_101_and_more_actions from (select userid from action where tenantid = 1 and actiontypeid= 1 and actiontime > @actiontime group by userid having count(1) > 100 and count(1) <= 100) u) u100


-- Return Conversion statistics = The number of items that where bought because
-- they were clicked in a recommendation before.
-- 10M entries/tenant: average computation time 1sec
SELECT @tenantid      :=1;
SELECT @actionTypeId  :=3; -- buy
SELECT COUNT(1) AS recommendationToBuyCount FROM (
    SELECT
        itemId, userid
    FROM
        action WHERE itemTypeId = 1 AND actionTypeId = @actionTypeId AND tenantid = @tenantid) a
    INNER JOIN
        (SELECT itemToId, userid FROM backtracking WHERE tenantid= @tenantid) b
        ON (a.itemId = b.itemToId AND a.userid= b.userid)




-- show the number of items that have the minimal support @minSupport.
-- 10M entries/tenant: average computation time 20sec
SELECT @tenantid     :=1;
SELECT @actionTypeId :=1; -- view
SELECT @minSupport   :=2; 

SELECT COUNT(1) AS itemsWithMinSupport
FROM (SELECT  itemId as item1action FROM action
WHERE tenantid=@tenantid AND itemtypeid = 1 AND actionTypeId = @actionTypeId
GROUP BY itemId HAVING COUNT(1)>=@minSupport) a


-- Get Items that are in at least @minSupport baskets
-- THIS query won't finish in a reasonable time
-- since 10M * 10M = 10*10*10^6*^10^6 = 10^14/2 entries to join
--
SELECT @tenantid     :=1;
SELECT @actiontypeId :=1;

-- item must be in 0,1% of all baskets:
-- SELECT @minSupport   := (SELECT Count(distinct userid) FROM ACTION)/1000 -- 1 minute/10M
SELECT @minSupport   := 500;

SELECT a1.itemid
FROM
    action a1 INNER JOIN action a2 ON (
        a1.tenantid=a2.tenantid AND
        a1.itemtypeid= a2.itemtypeid AND
        a1.actiontypeId = a2.actiontypeId AND
        a1.itemid = a2.itemid AND
        a1.userid <> a2.userid AND
        a1.tenantid = @tenantid AND
        a1.actiontypeId = @actiontypeId)
HAVING Count(a1.itemId) >= @minSupport;


-- Get the number of Items that are in at least @minSupport baskets (the dirty one)
-- 10M entries/tenant: average computation time 18 sec
--
-- The most acted items on, are the items considered for rulemining, which makes
-- sense, because the items watched most should have recommendations
SELECT @tenantid     :=1;
SELECT @actiontypeId :=1;
SELECT @minSupport   :=500;

SELECT count(1) FROM (
SELECT itemid
FROM action
WHERE
	tenantid = @tenantid AND
    actiontypeId = @actiontypeId
GROUP BY
	itemid
HAVING COUNT(itemid) >= @minSupport
) a



-- Number of total actions of the top L1 items  (=items with most actions
-- orderd by their action count)
-- 10M entries/tenant: average computation time 30sec
SELECT @tenantid  :=1;
SELECT @actiontypeId :=1;

SELECT SUM(c) FROM (
SELECT itemid, itemtypeid, tenantid, COUNT(1) as c
FROM action
WHERE
	tenantid = @tenantid AND
    actiontypeId = @actiontypeId
GROUP BY
	itemid, itemtypeid, tenantid
ORDER BY c desc
LIMIT 5000 -- L1 Size
) a



-- Number of total actions of items that are in the itemassoc table.
-- 10M entries/tenant: average computation time 18 seconds

SELECT @tenantid  :=1;
SELECT @actiontypeId :=1;

SELECT SUM(c) AS number_of_total_actions_of_items_in_itemassoc FROM (
SELECT c
FROM
(SELECT itemid, itemtypeid, tenantid, COUNT(1) as c
FROM action
WHERE
	tenantid = @tenantid AND
    actiontypeId = @actiontypeId
GROUP BY
	itemid
LIMIT 5000 -- L1 Size
) a INNER JOIN itemassoc i ON (
	i.itemfromid = a.itemid AND
    a.itemtypeid = i.itemFromTypeId AND
    a.tenantid   = i.tenantid)
GROUP BY
	a.itemid, a.itemtypeid, a.tenantid
) a



-- daily user activity
-- show number of actions per hour
--

SELECT @tenantid  :=1;
SELECT
    m, Sum(actions)
FROM
    (SELECT
        hour(actionTime) as m,
        COUNT(1) as actions
    FROM
        action
    WHERE
        tenantid = @tenantid
    GROUP BY hour(actionTime)
    ) a
GROUP BY m


-- Power users (bonus)
-- Select users with a given number of purchases
-- in a given time period
--
SELECT @tenantid  :=1;
SELECT @from  :="2009-01-01";
SELECT @to    := (SELECT now());
SELECT @actiontypeId :=3; -- BUY
SELECT @numberofactions := 5;

SELECT
    count(1) as buys_per_user,
    MIN(actionTime),
    MAX(actionTime),
    datediff(MAX(actionTime), MIN(actionTime)) as days_diff,
    userid
FROM action a
WHERE
    tenantid = @tenantid and
    actiontime>=@from and
    actiontime< @to AND
    actionTypeId = @actiontypeId
GROUP BY
    userid
HAVING
    count(1) >= @numberofactions;



-- Show customer details:
-- Number of Sessions,
-- Average Number of Actions per Session

SELECT @tenantid  :=1;

SELECT
  userid,
  AVG(actions_per_session),
  count(1) as sessions FROM (
SELECT
  count(1) as actions_per_session,
  m.stringid as userid,
  a.userid as userid_int,
  a.sessionid
FROM
  action a inner join idmapping m on (m.intid = a.userid)
WHERE
  tenantid = @tenantid
  -- AND INSTR(m.stringid,"c")>0
GROUP BY
  m.stringid,a.sessionid) a
GROUP BY
  userid