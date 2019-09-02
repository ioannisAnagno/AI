
findCost(_,HIGHWAY,_,LIT,LANES,MAXSPEED,_,_,_,_,_,_,_,_,_,_,TOLL,TIME:_,COST):-
	(HIGHWAY = motorway -> HI is 1; HIGHWAY = motorway_link -> HI is 1.2;HIGHWAY = primary -> HI is 1.2;HIGHWAY = primary_link -> HI is 1.5;HIGHWAY = residential -> HI is 1.8 ;HIGHWAY = secondary -> HI is 1.5; HIGHWAY = secondary_link -> HI is 1.8;HIGHWAY = trach -> HI is 4;HI is 2.2),
	(LANES = 1 -> LA is 2; LANES = 2 -> LA is 1.5; LA is 1),
	(MAXSPEED = e -> MA is 2;MAXSPEED =< 30 -> MA is 2; MAXSPEED =< 60 -> MA is 1.5; MA is 1),
	(TOLL = e -> TO is 0 ; TO is 2),
	(TIME >= 10, TIME =< 6 , LIT = e -> LIGHT = 2; LIGHT = 1),
	COST1 is HI * LA * MA * LIGHT,
	COST is COST1 + TO.

isDrivable(_,HIGHWAY,_,_,_,_,e,e,e,e,e,_,_,_,e,e,_) :-
	HIGHWAY \= path,
	HIGHWAY \= pedestrian,
	HIGHWAY \= footway,
	HIGHWAY \= steps,
	HIGHWAY \= bridleway.

isValidTaxi(yes,MinCapacity-MaxCapacity, NumberOfPersons) :-
	MaxCapacity >= NumberOfPersons,
	MinCapacity =< NumberOfPersons.

findTrafficCost(09:00-11:00=MO, 13:00-15:00=MI, 17:00-19:00=AF, HOUR:_, TC) :-
	(HOUR < 11 , HOUR >= 9 ->
	(MO = high -> TC = 2 ; TC = 1);
	HOUR < 15 , HOUR >= 13 ->
	(MI = high -> TC = 2 ; TC = 1);
	HOUR < 19 , HOUR >= 17 ->
	(AF = high -> TC = 2 ; TC = 1);
	(TC = 0)).

findRating(TaxiLanguages, TaxiRating, ClientLanguage, Rating) :-
	(member(ClientLanguage, TaxiLanguages) -> Penalty is 0 ; Penalty is 3),
	Rating is TaxiRating - Penalty.
