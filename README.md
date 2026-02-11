# WesNot


## Description
jeu de stratégie tour par tour avec une forte composante rogue like, développé dans le cadre d'un projet scolaire.

Le jeu se lance en utilisant les commandes `javac` et `java` pour compiler et exécuter le code.

## Contribution personnel
#### Génération procédural de donjon :
  - Binary Space Partitionning : découper récursivement la zone d'affichage en deux pour créer un arbre avec étiquette aux feuilles contenant les dimensions du rectangles considéré. Ainsi, la racine contient les dimensions de l'écran et les feuilles contiennent les dimensions d'une pièce du donjon. Le découpage suit une loi normale.
  - déssiner les dîtes pièces
  - relier les pièces de proche en proche en descendant l'arbre afin d'éviter les cycles
#### Génération procédural de caverne :
  - Cellular Automaton : initialiser chaque tuile avec un état (mort ou vivant); laisser tourner plusieurs itérations de règle pour déterminer l'état d'une tuile en fonction de ses voisins
  - Flood Fill pour récupérer les zones ouvertes (remarque : complexité améliorable en construisant le graphe depuis l'automate)
  - Pathfinding et Kruskal implémenter avec une Union Find pour relier n zones ouvertes par un plus court chemin sans cycle
#### Gestionnaire de Dialogue :
lire un fichier formaté pour en extraire des dialogues à afficher, typing-machine style 
#### Seeding et chunk
2^64 carte à explorer et possiblité de passer de l'une à l'autre en rejoignant les bords
#### Agent décisionel 
entité non jouable considérant une liste de tâche pondérés par un "comportement" (agressif, peureux, ...) 

## Distributuion
tout a été tésté sur une distribution windows. Des problèmes, notamment d'affichage peuvent apparaitre sur d'autre distribution.

## Instructions pour lancer le jeu

### Compilation
Pour compiler le jeu, exécutez la commande suivante depuis le répertoire racine du projet en Linux:
```sh
javac -d out -cp src src/**/*.java
```
ou bien sur PowerShell:

```sh
javac -d out -cp src (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

### Exécution
Une fois la compilation terminée, vous pouvez lancer le jeu avec la commande suivante :
```sh
java -cp out launcher.Launcher
```

## Authors
développement :
- Ilyes El Mouhtadi
- Selim Khattab
- Louis Rialland
- Marceau Léna--Schroll
- Vincent Schoonheere 

visuel :
- Malo Ferrari 

#### Attention:

Il sera peut être mieux de cacher le task-bar pendant l'éxécution du jeu pour éviter de cacher certains boutons.

