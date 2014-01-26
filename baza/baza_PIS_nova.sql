SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `purgardb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `purgardb`;

-- -----------------------------------------------------
-- Table `purgardb`.`vrPartnera`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`vrPartnera` (
  `idVrsta` INT NOT NULL ,
  `vrsta` VARCHAR(15) NULL ,
  PRIMARY KEY (`idVrsta`) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`partner`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`partner` (
  `idPartner` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idVrsta` INT NOT NULL ,
  `naziv` VARCHAR(20) NULL ,
  `ime` VARCHAR(15) NULL ,
  `prezime` VARCHAR(20) NULL ,
  `telefon` VARCHAR(20) NULL ,
  `mjesto` VARCHAR(15) NULL ,
  `postBroj` INT UNSIGNED NULL ,
  `adresa` VARCHAR(40) NULL ,
  `maticniBr` VARCHAR(15) NULL ,
  `ziroRac` VARCHAR(20) NULL ,
  PRIMARY KEY (`idPartner`) ,
  INDEX `fk_partner_vrPartnera` (`idVrsta` ASC) ,
  CONSTRAINT `fk_partner_vrPartnera`
    FOREIGN KEY (`idVrsta` )
    REFERENCES `purgardb`.`vrPartnera` (`idVrsta` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`ponuda`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`ponuda` (
  `idPonuda` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idPartner` INT UNSIGNED NOT NULL ,
  `datumKreiranja` DATETIME NULL ,
  `mjestoKreiranja` VARCHAR(25) NULL ,
  `opisHeader` TEXT NULL ,
  `opisFooter` TEXT NULL ,
  `stat` INT UNSIGNED NULL ,
  PRIMARY KEY (`idPonuda`) ,
  INDEX `ponuda_FKIndex1` (`idPartner` ASC) ,
  CONSTRAINT `fk_{3086AB6A-9FD9-4269-AF08-C08584D747C4}`
    FOREIGN KEY (`idPartner` )
    REFERENCES `purgardb`.`partner` (`idPartner` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`racun`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`racun` (
  `idRacuna` INT NOT NULL AUTO_INCREMENT ,
  `idPartner` INT UNSIGNED NOT NULL ,
  `datumIzdavanja` DATETIME NULL ,
  `mjestoIzdavanja` VARCHAR(25) NULL ,
  `opisHeader` TEXT NULL ,
  `opisFooter` TEXT NULL ,
  PRIMARY KEY (`idRacuna`) ,
  INDEX `fk_racun_partner` (`idPartner` ASC) ,
  CONSTRAINT `fk_racun_partner`
    FOREIGN KEY (`idPartner` )
    REFERENCES `purgardb`.`partner` (`idPartner` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`stavkaPonuda`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`stavkaPonuda` (
  `idStPon` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idPonuda` INT UNSIGNED NULL ,
  `idRacuna` INT NULL ,
  `opis` VARCHAR(50) NOT NULL ,
  `dimenzije` VARCHAR(15) NULL ,
  `kolicina` INT UNSIGNED NOT NULL ,
  `jedCijena` FLOAT NOT NULL ,
  PRIMARY KEY (`idStPon`) ,
  INDEX `fk_stavkaPonuda_ponuda` (`idPonuda` ASC) ,
  INDEX `fk_stavkaPonuda_racun` (`idRacuna` ASC) ,
  CONSTRAINT `fk_stavkaPonuda_ponuda`
    FOREIGN KEY (`idPonuda` )
    REFERENCES `purgardb`.`ponuda` (`idPonuda` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_stavkaPonuda_racun`
    FOREIGN KEY (`idRacuna` )
    REFERENCES `purgardb`.`racun` (`idRacuna` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`privilegije`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`privilegije` (
  `idPristup` INT NOT NULL ,
  `naziv` VARCHAR(15) NULL ,
  PRIMARY KEY (`idPristup`) )
ENGINE = MyISAM
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`zaposlenici`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`zaposlenici` (
  `idZap` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idPristup` INT NOT NULL ,
  `korIme` VARCHAR(20) NOT NULL ,
  `lozinka` VARCHAR(20) NOT NULL ,
  `ime` VARCHAR(15) NULL ,
  `prezime` VARCHAR(20) NULL ,
  `telefon` VARCHAR(20) NULL ,
  `mjesto` VARCHAR(15) NULL ,
  `postBroj` INT UNSIGNED NULL ,
  `adresa` VARCHAR(40) NULL ,
  `matBroj` VARCHAR(15) NULL ,
  `ziroRacun` VARCHAR(20) NULL ,
  PRIMARY KEY (`idZap`) ,
  INDEX `fk_zaposlenici_privilegije` (`idPristup` ASC) ,
  CONSTRAINT `fk_zaposlenici_privilegije`
    FOREIGN KEY (`idPristup` )
    REFERENCES `purgardb`.`privilegije` (`idPristup` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`otpremnica`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`otpremnica` (
  `idOtp` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idRacuna` INT NULL ,
  `datum` DATETIME NULL ,
  `izdao` INT UNSIGNED NULL ,
  `nacin` VARCHAR(20) NULL ,
  PRIMARY KEY (`idOtp`) ,
  INDEX `otpremnica_FKIndex2` (`izdao` ASC) ,
  INDEX `fk_otpremnica_racun` (`idRacuna` ASC) ,
  CONSTRAINT `fk_{289A2870-CA67-443A-9160-D0607976A189}`
    FOREIGN KEY (`izdao` )
    REFERENCES `purgardb`.`zaposlenici` (`idZap` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_otpremnica_racun`
    FOREIGN KEY (`idRacuna` )
    REFERENCES `purgardb`.`racun` (`idRacuna` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`stavkaOtpremnice`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`stavkaOtpremnice` (
  `idOtp` INT UNSIGNED NOT NULL ,
  `idStPon` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`idOtp`, `idStPon`) ,
  INDEX `stavkaPonuda_has_otpremnica_FKIndex2` (`idOtp` ASC) ,
  INDEX `fk_stavkaOtpremnice_stavkaPonuda` (`idStPon` ASC) ,
  CONSTRAINT `fk_{88AE5041-279D-4EDD-9573-EAF1FCAA64A9}`
    FOREIGN KEY (`idOtp` )
    REFERENCES `purgardb`.`otpremnica` (`idOtp` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_stavkaOtpremnice_stavkaPonuda`
    FOREIGN KEY (`idStPon` )
    REFERENCES `purgardb`.`stavkaPonuda` (`idStPon` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;

USE `purgardb`;

-- -----------------------------------------------------
-- Data for table `purgardb`.`zaposlenici`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `zaposlenici` (`idZap`, `idPristup`, `korIme`, `lozinka`, `ime`, `prezime`, `telefon`, `mjesto`, `postBroj`, `adresa`, `matBroj`, `ziroRacun`) VALUES (0, 0, 'administrator', 'administrator', '', '', '', '', 0, '', '', '');

COMMIT;

-- -----------------------------------------------------
-- Data for table `purgardb`.`vrPartnera`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `vrPartnera` (`idVrsta`, `vrsta`) VALUES (0, 'Kupac');
INSERT INTO `vrPartnera` (`idVrsta`, `vrsta`) VALUES (1, 'Dobavljač');

COMMIT;

-- -----------------------------------------------------
-- Data for table `purgardb`.`privilegije`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO `privilegije` (`idPristup`, `naziv`) VALUES (0, 'administrator');
INSERT INTO `privilegije` (`idPristup`, `naziv`) VALUES (1, 'vlasnik');
INSERT INTO `privilegije` (`idPristup`, `naziv`) VALUES (2, 'zaposlenik');

COMMIT;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
