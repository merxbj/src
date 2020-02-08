IF EXISTS (SELECT * FROM tempdb..sysobjects
                  WHERE id = OBJECT_ID('tempdb..#grand_master') AND type = 'U')
        DROP TABLE #grand_master

CREATE TABLE #grand_master (
	[position] [int] NOT NULL,
	[name] [nvarchar](max) NOT NULL,
	[race] [varchar](10) NOT NULL,
	[joined] [varchar](10) NOT NULL,
	[points] [int] NOT NULL,
	[wins] [nchar](10) NOT NULL,
	[loses] [nchar](10) NOT NULL,
)

BULK INSERT #grand_master
FROM 'c:\Users\JM185267\Desktop\gm\gmcsv.csv'
WITH
	(
		FIELDTERMINATOR= ',',
		ROWTERMINATOR = '\n',
		DATAFILETYPE = 'widechar'
	)
	
INSERT INTO grand_master
SELECT tgm.pos, tgm.nam, tgm.race, tgm.jnd, tgm.pts, tgm.w, tgm.l
FROM
(
	SELECT
		position AS pos,
		name AS nam,
		race AS race,
		CONVERT(DATE, joined, 103) AS jnd,
		points AS pts,
		wins AS w,
		loses AS l
	FROM #grand_master
) AS tgm