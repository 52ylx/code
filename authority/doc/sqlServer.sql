--drop FUNCTION toJson
create FUNCTION [dbo].[toJson]( @JSON NVARCHAR(MAX))
	RETURNS @hierarchy TABLE(element_id INT IDENTITY(1, 1) NOT NULL, sequenceNo [int] NULL, parent_ID INT,Object_ID INT,NAME NVARCHAR(2000),StringValue NVARCHAR(MAX) NOT NULL,ValueType VARCHAR(10) NOT null)
	AS
BEGIN
	DECLARE @FirstObject INT,@OpenDelimiter INT,@NextOpenDelimiter INT,@NextCloseDelimiter INT, @Type NVARCHAR(10), @NextCloseDelimiterChar CHAR(1), @Contents NVARCHAR(MAX), @start INT,
	    @end INT, @param INT, @EndOfName INT, @token NVARCHAR(200), @value NVARCHAR(MAX), @SequenceNo int = 0, @name NVARCHAR(200),
	    @parent_ID INT = 0,@lenJSON INT,@characters NCHAR(36)='0123456789abcdefghijklmnopqrstuvwxyz',@result BIGINT,@index SMALLINT,@Escape INT
DECLARE @Strings TABLE ( String_ID INT IDENTITY(1, 1),StringValue NVARCHAR(MAX))
WHILE 1=1
	BEGIN
		SELECT @start=PATINDEX('%[^a-zA-Z]["]%', @json collate SQL_Latin1_General_CP850_Bin)
		IF @start=0 BREAK
		IF SUBSTRING(@json, @start+1, 1)='"'
			BEGIN
				SET @start=@start+1
				SET @end=PATINDEX('%[^\]["]%', RIGHT(@json, LEN(@json+'|')-@start) collate SQL_Latin1_General_CP850_Bin)
			END
	IF @end=0 BREAK
	SELECT @token=SUBSTRING(@json, @start+1, @end-1)
	SELECT @token=REPLACE(@token, FROMString, TOString) FROM (SELECT '\"' AS FromString, '"' AS ToString UNION ALL SELECT '\\', '\' UNION ALL SELECT '\/', '/' UNION ALL SELECT '\b', CHAR(08)UNION ALL SELECT '\f', CHAR(12)UNION ALL SELECT '\n', CHAR(10)UNION ALL SELECT '\r', CHAR(13)UNION ALL SELECT '\t', CHAR(09)) substitutions
	SELECT @result=0, @escape=1
	WHILE @escape>0
		BEGIN
			SELECT @index=0,@escape=PATINDEX('%\x[0-9a-f][0-9a-f][0-9a-f][0-9a-f]%', @token collate SQL_Latin1_General_CP850_Bin)
			IF @escape>0
				BEGIN
					WHILE @index<4
						BEGIN
							SELECT @result=@result+POWER(16, @index)*(CHARINDEX(SUBSTRING(@token, @escape+2+3-@index, 1),@characters)-1), @index=@index+1
						END
					SELECT @token=STUFF(@token, @escape, 6, NCHAR(@result))
				END
		END
	INSERT INTO @Strings (StringValue) SELECT @token
	SELECT @JSON=STUFF(@json, @start, @end+1,'@string'+CONVERT(NVARCHAR(5), @@identity))
END

WHILE 1=1
	BEGIN
	SELECT @parent_ID=@parent_ID+1
	SELECT @FirstObject=PATINDEX('%[{[[]%', @json collate SQL_Latin1_General_CP850_Bin)
	IF @FirstObject = 0 BREAK
	IF (SUBSTRING(@json, @FirstObject, 1)='{') SELECT @NextCloseDelimiterChar='}', @type='object' ELSE SELECT @NextCloseDelimiterChar=']', @type='array'
	SELECT @OpenDelimiter=@firstObject
	WHILE 1=1
		BEGIN
			SELECT @lenJSON=LEN(@JSON+'|')-1
			SELECT @NextCloseDelimiter=CHARINDEX(@NextCloseDelimiterChar, @json,@OpenDelimiter+1)
			SELECT @NextOpenDelimiter=PATINDEX('%[{[[]%', RIGHT(@json, @lenJSON-@OpenDelimiter)collate SQL_Latin1_General_CP850_Bin)
			IF @NextOpenDelimiter=0 BREAK
			SELECT @NextOpenDelimiter=@NextOpenDelimiter+@OpenDelimiter
			IF @NextCloseDelimiter<@NextOpenDelimiter BREAK
			IF SUBSTRING(@json, @NextOpenDelimiter, 1)='{' SELECT @NextCloseDelimiterChar='}', @type='object' ELSE SELECT @NextCloseDelimiterChar=']', @type='array'
			SELECT @OpenDelimiter=@NextOpenDelimiter
		END

	SELECT @contents=SUBSTRING(@json, @OpenDelimiter+1,@NextCloseDelimiter-@OpenDelimiter-1)
	SELECT @JSON=STUFF(@json, @OpenDelimiter,@NextCloseDelimiter-@OpenDelimiter+1,'@'+@type+CONVERT(NVARCHAR(5), @parent_ID))
	WHILE (PATINDEX('%[A-Za-z0-9@+.e]%', @contents collate SQL_Latin1_General_CP850_Bin))<>0
		BEGIN
			IF @Type='Object'
				BEGIN
					SELECT @SequenceNo=0,@end=CHARINDEX(':', ' '+@contents)
					SELECT  @start=PATINDEX('%[^A-Za-z@][@]%', ' '+@contents collate SQL_Latin1_General_CP850_Bin)
					SELECT @token=SUBSTRING(' '+@contents, @start+1, @End-@Start-1),
						@endofname=PATINDEX('%[0-9]%', @token collate SQL_Latin1_General_CP850_Bin),
						@param=RIGHT(@token, LEN(@token)-@endofname+1)
					SELECT
						@token=LEFT(@token, @endofname-1),
						@Contents=RIGHT(' '+@contents, LEN(' '+@contents+'|')-@end-1)
					SELECT  @name=stringvalue FROM @strings
						WHERE string_id=@param --fetch the name
				END
			ELSE
SELECT @Name=null,@SequenceNo=@SequenceNo+1
		SELECT @end=CHARINDEX(',', @contents)
		IF @end=0
		IF ISNUMERIC(@contents) = 1
			SELECT @end = LEN(@contents)
		Else
			SELECT  @end=PATINDEX('%[A-Za-z0-9@+.e][^A-Za-z0-9@+.e]%', @contents+' ' collate SQL_Latin1_General_CP850_Bin) + 1
			SELECT @start=PATINDEX('%[^A-Za-z0-9@+.e][A-Za-z0-9@+.e]%', ' '+@contents collate SQL_Latin1_General_CP850_Bin)
			IF ISNUMERIC(@contents)=1 SELECT @end +=1
			SELECT @Value=RTRIM(SUBSTRING(@contents, @start, @End-@Start)),@Contents=RIGHT(@contents+' ', LEN(@contents+'|')-@end)
			IF SUBSTRING(@value, 1, 7)='@object'
				INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, Object_ID, ValueType) SELECT @name, @SequenceNo, @parent_ID, SUBSTRING(@value, 8, 5),SUBSTRING(@value, 8, 5), 'object'
			ELSE
				IF SUBSTRING(@value, 1, 6)='@array'
					INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, Object_ID, ValueType) SELECT @name, @SequenceNo, @parent_ID, SUBSTRING(@value, 7, 5),SUBSTRING(@value, 7, 5), 'array'
				ELSE
					IF SUBSTRING(@value, 1, 7)='@string'
						INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, ValueType) SELECT @name, @SequenceNo, @parent_ID, stringvalue, 'string' FROM @strings WHERE string_id=SUBSTRING(@value, 8, 5)
					ELSE
						IF @value IN ('true', 'false')
							INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, ValueType) SELECT @name, @SequenceNo, @parent_ID, @value, 'boolean'
						ELSE
							IF @value='null'
								INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, ValueType) SELECT @name, @SequenceNo, @parent_ID, @value, 'null'
							ELSE
								IF PATINDEX('%[^0-9]%', @value collate SQL_Latin1_General_CP850_Bin)>0
									INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, ValueType) SELECT @name, @SequenceNo, @parent_ID, @value, 'real'
								ELSE
									INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, ValueType) SELECT @name, @SequenceNo, @parent_ID, @value, 'int'
			if @Contents=' ' Select @SequenceNo=0
		END
	END
	INSERT INTO @hierarchy (NAME, SequenceNo, parent_ID, StringValue, Object_ID, ValueType) SELECT '-',1, NULL, '', @parent_id-1, @type

  RETURN
END


create FUNCTION [dbo].[createSqlstr](@json VARCHAR(max))
	RETURNS @sqlTable TABLE ([sql] nvarchar(max))
AS
BEGIN
declare @tables TABLE(element_id INT , sequenceNo [int] NULL, parent_ID INT,Object_ID INT,NAME NVARCHAR(2000),StringValue NVARCHAR(MAX) NOT NULL,ValueType VARCHAR(10) NOT null)
DECLARE @paraTable TABLE(k VARCHAR(max), v VARCHAR(max) , t VARCHAR(max))
DECLARE @whereTable TABLE(k VARCHAR(max), v VARCHAR(max) , t VARCHAR(max))
DECLARE @sql1 VARCHAR(max)='',@sql2  VARCHAR(max)='',@sql  nvarchar(max)='', @tableName VARCHAR(500) = '', @sqlType VARCHAR(50) ='' ,@order VARCHAR(500) = '' , @maxID int = 0,@Count int =0,@page VARCHAR(100),@limit VARCHAR(100)
insert into @tables select * from toJson(@json)
select @maxID = Object_ID from @tables where parent_ID is null
select @tableName = StringValue from @tables where parent_ID = @maxID and Name = 'tableName'
select @sqlType = StringValue from @tables where parent_ID = @maxID and Name = 'sqlType'
select @order = StringValue from @tables where parent_ID = @maxID and Name = 'order'
insert into @paraTable
SELECT
	map.k ,map.v ,case when b.name in('bigint' ,'decimal','money','smallmoney','float','int','numeric','real','smallint','tinyint') then 'num'
	when b.name in('datetime','time','smalldatetime','date','datetime2') then 'date' when b.name in('uniqueidentifier') then 'id' else 'string' end t
	FROM  syscolumns a
	left join systypes b on a.xtype=b.xusertype
	inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name = @tableName
	join (select Name k ,StringValue v from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'para') )map on map.k = a.name
	where b.name is not null
	union ALL select '@1',map.v,'string' from (select Name k ,StringValue v from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'para') )map where map.k = '@1'
insert into @whereTable
SELECT
	map.k ,map.v ,case when b.name in('bigint' ,'decimal','money','smallmoney','float','int','numeric','real','smallint','tinyint') then 'num'
	when b.name in('datetime','time','smalldatetime','date','datetime2') then 'date' when b.name in('uniqueidentifier') then 'id' else 'string' end t
	FROM  syscolumns a
	left join systypes b on a.xtype=b.xusertype
	inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name = @tableName
	join (select Name k ,StringValue v from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'where') )map on map.k = a.name
	where b.name is not null
	union ALL select '@1',map.v,'string' from (select Name k ,StringValue v from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'where') )map where map.k = '@1'

IF(@sqlType = 'select')
BEGIN
	select @sql1+=','+ case when (select PATINDEX('%[^0-9]%', v ) )= 0 then case when t ='date' then 'convert(varchar ,'+k+','+v+')' + k else k end when len(v)>0 then k+' '+v else v END from @paraTable
	select @sql2+=' and '+ case k when '@1' then v else case when t = 'id' and len(v)!=36 then k+' = null' else k+'='''+v+'''' end end from @whereTable
	if(LEN(@sql1)>1)set @sql1 = SUBSTRING(@sql1,2,LEN(@sql1)-1)		--拼接set语句
	else set @sql1 = '*'
	if(LEN(@sql2)>1)set @sql2 = SUBSTRING(@sql2,5,LEN(@sql2)-4)		--拼接where条件
	set @sql = 'select ' +@sql1 +' from '+ @tableName
	if(LEN(@sql2)>1) set @sql += ' where ' + @sql2
	if(len(@order)>1) set @sql += ' order by '+@order
END
else IF(@sqlType = 'edit')
BEGIN
	select @Count = count(1) from @whereTable
	if (@Count = 0)
		begin --没有where 新增
				select @sql1+=','+ k , @sql2+=','+ case when t='num' then v when t ='date' and v='1' then 'GETDATE()' when t ='id' and v='1' then 'NEWID()' else  ''''+v+'''' end from @paraTable
			if(LEN(@sql1)>1)set @sql1 = SUBSTRING(@sql1,2,LEN(@sql1)-1)
			if(LEN(@sql2)>1)set @sql2 = SUBSTRING(@sql2,2,LEN(@sql2)-1)
			set @sql = 'insert into ' + @tableName +' ( '+@sql1+' ) values ( ' + @sql2 + ')'
		end
	else
		BEGIN
			select @sql1+=','+k+'='+ case when t='num' then v when t ='date' and v='1' then 'GETDATE()' when t ='id' and v='1' then 'NEWID()' when t ='id' and v='' then 'null' else  ''''+v+'''' end from @paraTable
			select @sql2+=' and '+ case k when '@1' then v else case when t = 'id' and len(v)!=36 then k+' = null' else k+'='''+v+'''' end end from @whereTable
			if(LEN(@sql1)>1)set @sql1 = SUBSTRING(@sql1,2,LEN(@sql1)-1)		--拼接set语句
			if(LEN(@sql2)>1)set @sql2 = SUBSTRING(@sql2,5,LEN(@sql2)-4)		--拼接where条件
			set @sql = 'update ' + @tableName +' set '+@sql1+' where ' + @sql2	--拼接update语句
			if(1>LEN(@sql2)) set @sql = 'select '' '' sql ,''@0'' sqlStatus , ''暂不支持where条件为空的更新语句'' msg '
		END
END
else IF(@sqlType = 'del')
BEGIN
	select @sql2+=' and '+ case k when '@1' then v else case when t = 'id' and len(v)!=36 then k+' = null' else k+'='''+v+'''' end end from @whereTable
	if(LEN(@sql2)>1)set @sql ='delete '+@tableName +' where ' + SUBSTRING(@sql2,5,LEN(@sql2)-4)
	if(1>LEN(@sql2)) set @sql = 'select '' '' sql ,''@0'' sqlStatus , ''暂不支持where条件为空的更新语句'' msg '
END
else if(@sqlType = 'count')										--查询语句
	begin
			select @sql2+=' and '+ case k when '@1' then v else case when t = 'id' and len(v)!=36 then k+' = null' else k+'='''+v+'''' end end from @whereTable
			if(LEN(@sql2)>1)set @sql2 = SUBSTRING(@sql2,5,LEN(@sql2)-4)		--拼接where条件
			set @sql = 'select count(1) count from '+ @tableName
			if(LEN(@sql2)>1) set @sql += ' where ' + @sql2
	end
	else if(@sqlType = 'page')										--查询语句
	begin
			select @page=StringValue from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'para') and name = 'page'
			select @limit=StringValue from @tables where parent_ID = (select Object_ID from @tables where parent_ID = @maxID and Name = 'para') and name = 'limit'
			select @sql1+=','+ case when (select PATINDEX('%[^0-9]%', v ) )= 0 then case when t ='date' then 'convert(varchar ,'+k+','+v+')' + k else k end when len(v)>0 then k+' '+v else v END from @paraTable
			select @sql2+=' and '+ case k when '@1' then v else case when t = 'id' and len(v)!=36 then k+' = null' else k+'='''+v+'''' end end from @whereTable
			if(LEN(@sql1)>1)set @sql1 = SUBSTRING(@sql1,2,LEN(@sql1)-1)		--拼接set语句
			else set @sql1 = '*'
			if(LEN(@sql2)>1)set @sql2 = SUBSTRING(@sql2,5,LEN(@sql2)-4)		--拼接where条件
			set @sql ='select top '+@limit+' *
			from (select row_number()
			over(order by '+@order+') as rownumber,' +@sql1 +' from '+ @tableName
			if(LEN(@sql2)>1) set @sql += ' where ' + @sql2
			set @sql+=') temp_row
			where rownumber>(('+@page+'-1)*'+@limit+')'
	end
	insert into @sqlTable ([sql]) values (@sql)

	RETURN;
	END