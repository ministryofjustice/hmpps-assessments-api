-- noinspection SqlResolveForFile
set schema hmppsassessmentsapi;

DELETE FROM assessed_episode WHERE true;
DELETE FROM author WHERE true;
DELETE FROM offence WHERE true;
DELETE FROM assessment WHERE true;
DELETE FROM subject WHERE true;
