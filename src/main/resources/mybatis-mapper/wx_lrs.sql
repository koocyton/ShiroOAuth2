
CREATE TABLE `account` (
  `id` bigint(18) unsigned NOT NULL COMMENT '主 ID',
  `platform` enum('local','weixin','facebook','weibo','twitter') NOT NULL COMMENT '第三方平台',
  `platform_uid` varchar(100) NOT NULL COMMENT '第三方平台 ID',
  `verified` tinyint(1) NOT NULL COMMENT '邮箱是否验证通过的账号，第三方登陆，默认通过(1)',
  `account` varchar(250) NOT NULL COMMENT '邮箱或第三方 ID',
  `password` varchar(32) NOT NULL COMMENT '密码 md5(manager + '' & '' + source_password)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `platform_uid` (`platform`,`platform_uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账号';

INSERT INTO `account` (`id`, `platform`, `platform_uid`, `verified`, `account`, `password`, `created_at`)
VALUES
  (957995474248404992,'weixin','weixin_on7310Gxw-hpARQhIdREIUIAeC9Q',1,'weixin_on7310Gxw-hpARQhIdREIUIAeC9Q','','2018-01-29 15:14:54'),
  (963709784132030465,'local','local_963709784132030464',0,'test_4@gmail.com','fed1c2d1e2bbf7308e8e566ca2c35db7','2018-02-14 09:41:32'),
  (963709784132030467,'local','local_963709784132030466',0,'test_5@gmail.com','6c3f67cbae1289197fb8fad527ca943d','2018-02-14 09:41:32'),
  (963709784157196289,'local','local_963709784157196288',0,'test_3@gmail.com','df78117a763ee9fbb1cb0c5d63fcbb76','2018-02-14 09:41:32'),
  (963709784161390593,'local','local_963709784161390592',0,'test_2@gmail.com','a40d75ac9ee85de8111e6fdb42f28124','2018-02-14 09:41:32'),
  (963709784182362113,'local','local_963709784182362112',0,'test_0@gmail.com','3b94d459a3e08482d74d8bede06e00ed','2018-02-14 09:41:32'),
  (963709784186556417,'local','local_963709784186556416',0,'test_1@gmail.com','f634daabb490a553f8676b015a707fdb','2018-02-14 09:41:32'),
  (963709787764297729,'local','local_963709787764297728',0,'test_6@gmail.com','c464ed61d71185d589494dcd0be5b911','2018-02-14 09:41:32'),
  (963709787768492034,'local','local_963709787768492032',0,'test_7@gmail.com','ec362ada8cb95b2f263714b47a5d88ab','2018-02-14 09:41:32'),
  (963709787768492035,'local','local_963709787768492033',0,'test_8@gmail.com','f906c36eed00209cc24580e0ba987e78','2018-02-14 09:41:32'),
  (963709787793657857,'local','local_963709787793657856',0,'test_11@gmail.com','8dedbe7d53c67c18a554c6d3a8122ee6','2018-02-14 09:41:32'),
  (963709787810435073,'local','local_963709787810435072',0,'test_9@gmail.com','72466b576666c36d9333d126bf882f81','2018-02-14 09:41:32'),
  (963709787810435075,'local','local_963709787810435074',0,'test_10@gmail.com','2b5a1eecb2877fde940b4a446cb43c0e','2018-02-14 09:41:32'),
  (963711195720847361,'local','local_963711195720847360',0,'kton_1@gmail.com','5be3ec7d8fb7386878b4955f769a32bb','2018-02-14 09:47:08'),
  (963711195779567617,'local','local_963711195779567616',0,'kton_3@gmail.com','917c824385e4506f608835c262d6ed06','2018-02-14 09:47:08'),
  (963711195796344833,'local','local_963711195796344832',0,'kton_0@gmail.com','b7455a08b987648eb1554f5a4583d848','2018-02-14 09:47:08'),
  (963711195796344835,'local','local_963711195796344834',0,'kton_4@gmail.com','5dab068cf508019d64220d150fc9a650','2018-02-14 09:47:08'),
  (963711195850870785,'local','local_963711195850870784',0,'kton_5@gmail.com','89599b6cf40b183efff16a109ead2dfb','2018-02-14 09:47:08'),
  (963711195855065089,'local','local_963711195855065088',0,'kton_2@gmail.com','4ff58a335514f21b16b211c3e7ed30c8','2018-02-14 09:47:08'),
  (963711198216458241,'local','local_963711198216458240',0,'kton_6@gmail.com','2ae827df51dfe7a4d651fe1c22570389','2018-02-14 09:47:09'),
  (963711198229041153,'local','local_963711198229041152',0,'kton_7@gmail.com','42edfb7c47e4e364889421f8a8ec7cfc','2018-02-14 09:47:09'),
  (963711198229041155,'local','local_963711198229041154',0,'kton_8@gmail.com','35473e56a2df16075b349b3267e43432','2018-02-14 09:47:09'),
  (963711198233235457,'local','local_963711198233235456',0,'kton_11@gmail.com','eb9c03509b996e1f2629e98af7eaa494','2018-02-14 09:47:09'),
  (963711198291955713,'local','local_963711198291955712',0,'kton_9@gmail.com','37240df641405b61b69e95a410eeccd3','2018-02-14 09:47:09'),
  (963711198338093057,'local','local_963711198338093056',0,'kton_10@gmail.com','9441bfb019cf798ccb5e262b83b7391f','2018-02-14 09:47:09');

CREATE TABLE `user` (
  `id` bigint(18) unsigned NOT NULL COMMENT '主 ID',
  `nickname` varchar(250) NOT NULL COMMENT '昵称',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别',
  `avatar_url` text NOT NULL COMMENT '头像',
  `country` char(2) NOT NULL COMMENT '国家',
  `friends` text NOT NULL COMMENT '好友 ID',
  `score` int(11) NOT NULL COMMENT '得分',
  `ranking` int(11) NOT NULL COMMENT '排名',
  `gold` int(11) NOT NULL COMMENT '金币',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';

INSERT INTO `user` (`id`, `nickname`, `gender`, `avatar_url`, `country`, `friends`, `score`, `ranking`, `gold`)
VALUES
  (957995474248404992,'刘毅',1,'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJt18m6r8LJW6xWDvFGYeyeJyweebK46YwGc0zDts02f8ggUg3szAhf2d1dZwCUTmtib1FMTUibIDEA/0','CN','',0,0,0),
  (963709784132030465,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152105yvoqrgyspyvigrfg.jpg','CN','',0,0,0),
  (963709784132030467,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/1521069otv65rwwtozjvio.jpg','CN','',0,0,0),
  (963709784157196289,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152107r8hrodshndorry4h.jpg','CN','',0,0,0),
  (963709784161390593,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152108ywyrwewdc9vkqkm9.jpg','CN','',0,0,0),
  (963709784182362113,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152109d2b9406zzsq8zfsn.jpg','CN','',0,0,0),
  (963709784186556417,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152110eeohkhefmoof96of.jpg','CN','',0,0,0),
  (963709787764297729,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152112e1s00xsrehxszzh2.jpg','CN','',0,0,0),
  (963709787768492034,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152114pcnbbn9cbecxnf9v.jpg','CN','',0,0,0),
  (963709787768492035,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/1521150rtliwiyy66rii61.jpg','CN','',0,0,0),
  (963709787793657857,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152116d8llu51ub2pbfew2.jpg','CN','',0,0,0),
  (963709787810435073,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152207x5559h918sxm8fi0.jpg','CN','',0,0,0),
  (963709787810435075,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152208y888zqgxq8yyaj7p.jpg','CN','',0,0,0),
  (963711195720847361,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152209qyobdoqseymox2yz.jpg','CN','',0,0,0),
  (963711195779567617,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/1522102njn8c2zyqhqpba8.jpg','CN','',0,0,0),
  (963711195796344833,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152211xmu2e82zcswc8fsw.jpg','CN','',0,0,0),
  (963711195796344835,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/1522122u1fuf22qeo1nl3n.jpg','CN','',0,0,0),
  (963711195850870785,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152213394kr2cnaxea902a.jpg','CN','',0,0,0),
  (963711195855065089,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152214v5s8r5rr3l2ivtrs.jpg','CN','',0,0,0),
  (963711198216458241,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152217nb25ilzngei59be3.jpg','CN','',0,0,0),
  (963711198229041153,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152218yss55vdt5nhhnyvt.jpg','CN','',0,0,0),
  (963711198229041155,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/152221yu94eymy8juumn9u.jpg','CN','',0,0,0),
  (963711198233235457,'',1,'http://b.img.uwp.mobi/data/attachment/forum/201110/18/1522249qq09ky39fak1917.jpg','CN','',0,0,0),
  (963711198291955713,'',1,'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIamF3jz3e9hkqfjiasqJD1LvXpFa9ib90ohUrz1ZB6c6zHwM9fZNfwvhtswxGFC3gIGvJRrLmg4cAw/0','CN','',0,0,0),
  (963711198338093057,'',1,'https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI8447KD4c4LwL0y9bymnn01VziafrvXJos4XymqCAGbvsicO2Z5m30rvIXQP5yicnA8jtBsupkXDGpA/0','CN','',0,0,0);
