Prilikom kreiranja baze, baza (mysql) treba imati postavljen charset na UTF-8

treba dodati u konfiguracijsku datoteku my.cnf ili my.ini:
default-character-set=utf8

i ako baza i tablice nisu bile kreirane pod tim charsetom, treba ih alterati:

alter database mydatabase charset=utf8;
alter table mytable charset=utf8;