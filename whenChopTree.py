#!/usr/bin/pyhton3

#on land
if treeExistOnCurrentLand:
	if landExploredEntirely:
		if !hasRaft:
			if hasAxe:
				ChopTree
			elif mapHasAxe:
				FindAxe
				checkAgain
			else:
				doNothing
		else:
			goingToWater
	else:
		if QuestionMarkInRangeOfTree: # view at Tree point
		# explored the land that can be explored without any cutting tree
			if QuestionMarkBesideLand:
				ChopTree
			else: # QuestionMarkBesideWater
				doNothing
		else: # in the view there is no Question mark
			doNothing
else:
	doNothing


#on Water
if waterExploredEntirely:
	for p in LandingPoint:
		if p is EmptyPoint:
			landP
		elif p is tree:
			chekcAround(p)
			if AroundPExistEmptyPoint:
				landAtThatEmptyPoint
			elif AroundPExistAnotherTree:
				landPCutThatTree
		if EmptyPointOROtherStuff:
			landThere
		else:
else:
	continueWaterExploration

#be able to open door at exploration