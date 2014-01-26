SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `purgardb` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `purgardb`;

-- -----------------------------------------------------
-- Table `purgardb`.`vrPartnera`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`vrPartnera` (
  `idVrsta` INT NOT NULL AUTO_INCREMENT ,
  `vrsta` VARCHAR(15) NULL ,
  PRIMARY KEY (`idVrsta`) )
ENGINE = InnoDB
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
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`stavkaPonuda`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`stavkaPonuda` (
  `idStPon` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idPonuda` INT UNSIGNED NOT NULL ,
  `opis` VARCHAR(50) NOT NULL ,
  `dimenzije` VARCHAR(15) NULL ,
  `kolicina` INT UNSIGNED NOT NULL ,
  `jedCijena` FLOAT NOT NULL ,
  PRIMARY KEY (`idStPon`) ,
  INDEX `fk_stavkaPonuda_ponuda` (`idPonuda` ASC) ,
  CONSTRAINT `fk_stavkaPonuda_ponuda`
    FOREIGN KEY (`idPonuda` )
    REFERENCES `purgardb`.`ponuda` (`idPonuda` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`racun`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`racun` (
  `idRacuna` INT NOT NULL AUTO_INCREMENT ,
  `idPonuda` INT UNSIGNED NOT NULL ,
  `datumIzdavanja` DATETIME NULL ,
  `mjestoIzdavanja` VARCHAR(25) NULL ,
  `opisHeader` TEXT NULL ,
  `opisFooter` TEXT NULL ,
  `iznos` DOUBLE NULL ,
  PRIMARY KEY (`idRacuna`) ,
  INDEX `racun_FKIndex1` (`idPonuda` ASC) ,
  CONSTRAINT `fk_{F5B8EC28-3FA1-4C8A-8B52-6D859B3D628D}`
    FOREIGN KEY (`idPonuda` )
    REFERENCES `purgardb`.`ponuda` (`idPonuda` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`zaposlenici`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`zaposlenici` (
  `idZap` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `ime` VARCHAR(15) NULL ,
  `prezime` VARCHAR(20) NULL ,
  `telefon` VARCHAR(20) NULL ,
  `mjesto` VARCHAR(15) NULL ,
  `postBroj` INT UNSIGNED NULL ,
  `adresa` VARCHAR(40) NULL ,
  `matBroj` VARCHAR(15) NULL ,
  `ziroRacun` VARCHAR(20) NULL ,
  PRIMARY KEY (`idZap`) )
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`otpremnica`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`otpremnica` (
  `idOtp` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idPonuda` INT UNSIGNED NOT NULL ,
  `datum` DATETIME NULL ,
  `izdao` INT UNSIGNED NULL ,
  `nacin` VARCHAR(20) NULL ,
  PRIMARY KEY (`idOtp`) ,
  INDEX `otpremnica_FKIndex2` (`izdao` ASC) ,
  INDEX `fk_otpremnica_ponuda` (`idPonuda` ASC) ,
  CONSTRAINT `fk_{289A2870-CA67-443A-9160-D0607976A189}`
    FOREIGN KEY (`izdao` )
    REFERENCES `purgardb`.`zaposlenici` (`idZap` )
    ON DELETE RESTRICT
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_otpremnica_ponuda`
    FOREIGN KEY (`idPonuda` )
    REFERENCES `purgardb`.`ponuda` (`idPonuda` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;


-- -----------------------------------------------------
-- Table `purgardb`.`stavkaOtpremnice`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `purgardb`.`stavkaOtpremnice` (
  `idStPon` INT UNSIGNED NOT NULL ,
  `idOtp` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`idStPon`, `idOtp`) ,
  INDEX `stavkaPonuda_has_otpremnica_FKIndex1` (`idStPon` ASC) ,
  INDEX `stavkaPonuda_has_otpremnica_FKIndex2` (`idOtp` ASC) ,
  CONSTRAINT `fk_{1053CC5B-8AF7-4C14-9155-FFBAB2FE3125}`
    FOREIGN KEY (`idStPon` )
    REFERENCES `purgardb`.`stavkaPonuda` (`idStPon` )
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_{88AE5041-279D-4EDD-9573-EAF1FCAA64A9}`
    FOREIGN KEY (`idOtp` )
    REFERENCES `purgardb`.`otpremnica` (`idOtp` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci
PACK_KEYS = 0
ROW_FORMAT = DEFAULT;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
