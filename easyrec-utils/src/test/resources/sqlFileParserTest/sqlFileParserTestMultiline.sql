--
-- $Author: sat-rsa $
-- $Revision: 4 $
-- $Date: 2010-02-16 15:47:21 +0100 (Tue, 16 Feb 2010) $
--

//this file contains 5 identical sql statements and a lot of comments
select *
from dual;

select 
*
from 
dual; 

select --with comment
* --with comment
from //with comment
dual;#with comment

select --with comment
* --with comment
from //with comment
dual//comment again
;#with comment

select --with comment
* --with comment
from //with comment
dual;#with comment
