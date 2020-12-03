INSERT INTO APP_SERVER (ID,TYPE,HOST,PORT,DOMAIN,USERNAME,PASSWORD) VALUES (1,'smb','192.168.1.1',445,'','user','123456');

INSERT INTO App (NAME,AGENT,SERVER,ALPHA,TRIAL,RELEASE,IDENTIFIER)
VALUES ('测试App','android',1
,'/测试App/alpha/android'
, '/测试App/trial/android'
, '/测试App/release/android'
,'com.test.app');

INSERT INTO App (NAME,AGENT,SERVER,ALPHA,TRIAL,RELEASE,IDENTIFIER)
VALUES ('测试App','iOS',1
,'/测试App/alpha/iOS'
, '/测试App/trial/iOS'
, '/测试App/release/iOS'
,'com.test.app');



