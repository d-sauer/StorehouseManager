-- phpMyAdmin SQL Dump
-- version 2.11.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 07, 2009 at 10:30 AM
-- Server version: 3.23.32
-- PHP Version: 5.2.5


--
-- Database: `purgardb`
--

-- --------------------------------------------------------

--
-- Table structure for table `otpremnica`
--

CREATE TABLE IF NOT EXISTS `otpremnica` (
  `idOtp` int(10) unsigned NOT NULL auto_increment,
  `idPonuda` int(10) unsigned NOT NULL,
  `datum` datetime default NULL,
  `izdao` int(10) unsigned default NULL,
  `nacin` varchar(20) default NULL,
  PRIMARY KEY  (`idOtp`),
  KEY `otpremnica_FKIndex2` (`izdao`),
  KEY `fk_otpremnica_ponuda` (`idPonuda`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `otpremnica`
--

INSERT INTO `otpremnica` (`idOtp`, `idPonuda`, `datum`, `izdao`, `nacin`) VALUES
(1, 2, '2009-04-06 00:00:00', 2, 'VZ-334-AN'),
(3, 3, '2009-04-07 00:00:00', NULL, NULL),
(4, 1, '2009-04-07 00:00:00', 1, 'VZ-3434-as');

-- --------------------------------------------------------

--
-- Table structure for table `partner`
--

CREATE TABLE IF NOT EXISTS `partner` (
  `idPartner` int(10) unsigned NOT NULL auto_increment,
  `idVrsta` int(11) NOT NULL,
  `naziv` varchar(20) default NULL,
  `ime` varchar(15) default NULL,
  `prezime` varchar(20) default NULL,
  `telefon` varchar(20) default NULL,
  `mjesto` varchar(15) default NULL,
  `postBroj` int(10) unsigned default NULL,
  `adresa` varchar(40) default NULL,
  `maticniBr` varchar(15) default NULL,
  `ziroRac` varchar(20) default NULL,
  PRIMARY KEY  (`idPartner`),
  KEY `fk_partner_vrPartnera` (`idVrsta`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `partner`
--

INSERT INTO `partner` (`idPartner`, `idVrsta`, `naziv`, `ime`, `prezime`, `telefon`, `mjesto`, `postBroj`, `adresa`, `maticniBr`, `ziroRac`) VALUES
(1, 0, '', 'Josip', 'Broz', '091 887 6654', 'Zagreb', 10000, 'Ivana Gorana Kova?i?a 99b', '3009981301026', '242423'),
(2, 0, NULL, 'gfo', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `ponuda`
--

CREATE TABLE IF NOT EXISTS `ponuda` (
  `idPonuda` int(10) unsigned NOT NULL auto_increment,
  `idPartner` int(10) unsigned NOT NULL,
  `datumKreiranja` datetime default NULL,
  `mjestoKreiranja` varchar(25) default NULL,
  `opisHeader` text,
  `opisFooter` text,
  `stat` int(10) unsigned default NULL,
  PRIMARY KEY  (`idPonuda`),
  KEY `ponuda_FKIndex1` (`idPartner`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `ponuda`
--

INSERT INTO `ponuda` (`idPonuda`, `idPartner`, `datumKreiranja`, `mjestoKreiranja`, `opisHeader`, `opisFooter`, `stat`) VALUES
(1, 1, '2009-04-06 16:25:16', 'Novi Marof', 'Za izradu, dostavu i monta?u stolarskih radova prema prilo?enim nacrtima naru?ioca.', 'Namje?taj ?e biti iza?en, dostavljen i montiran na licu mjesta.\nU cijenu nisu ura?unate kvake i brave.\nPonuda se odnosi na vrata lakirana bijelim poluuretanskim dvokomponentnim lakom (polumat).', 1),
(2, 1, '2009-04-06 16:29:14', 'Novi Marof', '', '', 1),
(3, 1, '2009-04-06 16:41:03', 'Novi Marof', '', '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `racun`
--

CREATE TABLE IF NOT EXISTS `racun` (
  `idRacuna` int(11) NOT NULL auto_increment,
  `idPonuda` int(10) unsigned NOT NULL,
  `datumIzdavanja` datetime default NULL,
  `mjestoIzdavanja` varchar(25) default NULL,
  `opisHeader` text,
  `opisFooter` text,
  `iznos` double default NULL,
  PRIMARY KEY  (`idRacuna`),
  KEY `racun_FKIndex1` (`idPonuda`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `racun`
--

INSERT INTO `racun` (`idRacuna`, `idPonuda`, `datumIzdavanja`, `mjestoIzdavanja`, `opisHeader`, `opisFooter`, `iznos`) VALUES
(1, 2, '2009-04-06 16:31:23', 'Novi Marof', 'Za izradu, dostavu i monta?u stolarkih radova prema usmenom dogovoru sa naru?iocem.', 'Uplatu izvr?iti na ?iro ra?un XXXXX-XXXXXXXXXX', 414.799987792969);

-- --------------------------------------------------------

--
-- Table structure for table `stavkaotpremnice`
--

CREATE TABLE IF NOT EXISTS `stavkaotpremnice` (
  `idStPon` int(10) unsigned NOT NULL,
  `idOtp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idStPon`,`idOtp`),
  KEY `stavkaPonuda_has_otpremnica_FKIndex1` (`idStPon`),
  KEY `stavkaPonuda_has_otpremnica_FKIndex2` (`idOtp`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 PACK_KEYS=0;

--
-- Dumping data for table `stavkaotpremnice`
--

INSERT INTO `stavkaotpremnice` (`idStPon`, `idOtp`) VALUES
(1, 4),
(2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `stavkaponuda`
--

CREATE TABLE IF NOT EXISTS `stavkaponuda` (
  `idStPon` int(10) unsigned NOT NULL auto_increment,
  `idPonuda` int(10) unsigned NOT NULL,
  `opis` varchar(50) NOT NULL,
  `dimenzije` varchar(15) default NULL,
  `kolicina` int(10) unsigned NOT NULL,
  `jedCijena` float NOT NULL,
  PRIMARY KEY  (`idStPon`),
  KEY `fk_stavkaPonuda_ponuda` (`idPonuda`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `stavkaponuda`
--

INSERT INTO `stavkaponuda` (`idStPon`, `idPonuda`, `opis`, `dimenzije`, `kolicina`, `jedCijena`) VALUES
(1, 1, 'Vrata', '160 x 70', 2, 500),
(2, 2, 'Stol, okrugli', '70 x 70', 1, 340),
(3, 3, 'stol', NULL, 2, 300),
(6, 1, 'stol', NULL, 1, 340),
(7, 1, 'stolice', '4', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `vrpartnera`
--

CREATE TABLE IF NOT EXISTS `vrpartnera` (
  `idVrsta` int(11) NOT NULL auto_increment,
  `vrsta` varchar(15) default NULL,
  PRIMARY KEY  (`idVrsta`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `vrpartnera`
--

INSERT INTO `vrpartnera` (`idVrsta`, `vrsta`) VALUES
(0, 'Kupac'),
(1, 'Dobavljaè');

-- --------------------------------------------------------

--
-- Table structure for table `zaposlenici`
--

CREATE TABLE IF NOT EXISTS `zaposlenici` (
  `idZap` int(10) unsigned NOT NULL auto_increment,
  `ime` varchar(15) default NULL,
  `prezime` varchar(20) default NULL,
  `telefon` varchar(20) default NULL,
  `mjesto` varchar(15) default NULL,
  `postBroj` int(10) unsigned default NULL,
  `adresa` varchar(40) default NULL,
  `matBroj` varchar(15) default NULL,
  `ziroRacun` varchar(20) default NULL,
  PRIMARY KEY  (`idZap`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 PACK_KEYS=0 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `zaposlenici`
--

INSERT INTO `zaposlenici` (`idZap`, `ime`, `prezime`, `telefon`, `mjesto`, `postBroj`, `adresa`, `matBroj`, `ziroRacun`) VALUES
(1, 'Davor', 'Sauer', '091 8989 089', 'Na?ice', 31500, 'B.Josipa Jela?i?a 88a', '1206984301026', '233332'),
(2, 'Ivica', 'Zoric', '091 998 7664', 'Novi Marof', 42400, 'A.Stepinca 55', '0309984301026', '44332255'),
(3, 'Josip', 'Be?tek', '098 665 4432', 'Novi Marof', 42400, 'Ivana Gorana Kova?i?a 3', '0411984301026', '6677886');
