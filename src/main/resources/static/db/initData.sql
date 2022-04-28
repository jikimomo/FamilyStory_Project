select * from user;
select * from team;
select * from team_event;
select * from content;
select * from user_team;

insert into user values(1, '1980-04-30', '/userCoverImage/87128221567078.jpg', 1, 'bae1004kin@gmail.com', 1, '배정현', '아빠', 'bae1111', '010-1234-5678','bae',null);
insert into user values(2, '1982-07-21', null, 1, 'hye1004@naver.com', 1, '황혜련', '엄마', 'hye1111','010-1111-1111', 'hye', null);
insert into user values(3, '2004-04-29', null, 2, 'lee1004@gmail.com', 1, '이시연', '딸', 'lee1111', '010-1234-1234','lee', '/userProfileImage/86967627131403.jpg');
insert into user values(4, '2010-04-06', null, 1, 'kim1004@gmail.com', 1, '김창환', '아들', 'kim1111', '010-2222-2222', 'kim', null);
insert into user values(5, '2008-04-30', null, 3, 'eun1004@gmail.com', 3, '김은지', '사촌1', 'eun1111', '010-2222-2222', 'eun', null);
insert into user values(6, '2007-04-29', null, 3, 'cheon1004@gmail.com', 3, '천예원', '사촌2', 'cheon1111', '010-2222-2222', 'cheon', null);

insert into team values(1, 1, 'myFamily', '/TeamImage/879419000.jpg', '우리가족');
insert into team values(2, 3, 'onlyChildren', '/TeamImage/959096000.jpg', '자녀그룹');
insert into team values(3, 3, 'cousin', null, '사촌그룹');

insert into user_team values(1, '2020-10-20', true , 1, 1);
insert into user_team values(2, '2020-12-10', true,  1, 2);
insert into user_team values(3, '2020-12-10', true,  1, 3);
insert into user_team values(4, '2020-10-20', true , 1, 4);
insert into user_team values(5, '2020-10-20', true , 2, 3);
insert into user_team values(6, '2020-10-20', true , 2, 4);
insert into user_team values(7, '2020-10-20', true , 3, 3);
insert into user_team values(8, '2020-10-20', true , 3, 4);
insert into user_team values(9, '2020-10-20', true , 3, 5);
insert into user_team values(10, '2020-10-20', true , 3, 6);
insert into user_team values(11, '2020-10-20', false , 2, 6);

insert into team_event values(1, '2000-04-29', '부모님 결혼기념일', 1);
insert into team_event values(2, '2017-04-30', '우리가족 첫 해외여행', 1);
insert into team_event values(3, '2018-04-29', '동생 초등학교 입학', 2);
insert into team_event values(4, '2020-04-30', '사촌형제 첫 여행', 3);
insert into team_event values(5, '1998-04-28', '부모님 처음 만난 날', 1);

insert into content values(1, '한국 시리즈 직관', '잠실 야구장', '/uploadImage/87762264312776.jpg /uploadImage/87762265661345.jpg /uploadImage/87762277336363.jpg', '2022-04-28 23:04:27.924802', '2021-11-04', 1 1);
insert into content values(2, '봄맞이 산행', '지리산', '/uploadImage/87823489785070.jpg /uploadImage/87823490233023.jpg', '2022-04-28 23:05:29.136426', '2021-03-31', 1, 2);
insert into content values(3, '하교 후 놀이!', '집앞 놀이터', '/uploadImage/87921701013567.jpg /uploadImage/87921701528942.jpg', '2022-04-28 23:07:07.348502', '2022-03-02', 2, 3);