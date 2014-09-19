

# Cubes and Multidimensional modelling

AI borrows heavily from the vocabulary and concepts of multi-dimensional modelling that is long established
within the Business Intelligence (BI) domain.

Specifically the MDX standard provides most of the framework for analysis within AI.

The text bellow is heavily excerpted from the following documents:
*  [TUTORIAL: Introduction to Multidimensional 
   Expressions (MDX)] (http://www.fing.edu.uy/inco/grupos/csi/esp/Cursos/cursos_act/2005/DAP_SistDW/Material/2-SDW-Laboratorio1-2005.pdf)
   

## Cube Concepts

Cubes are key elements in online analytic processing. They are subsets of data from 
the OLAP store, organized and summarized into multidimensional structures. These 
data summaries provide the mechanism that allows rapid and uniform response 
times to complex queries.

The fundamental cube concepts to understand are _dimensions_ and _measures_.

### Dimensions

_Dimensions_ provide the categorical descriptions by which the measures are 
   separated for analysis. Examples of dimensions include:
   
   + Time
   + Geography
   + Donor
   + Beneficiary Type
   + Activity
   
### Measures

_Measures_ identify the numerical values that are summarized for analysis, such as 
number of water users, number of kits distributed, water quality, etc.
 
An important point to note here: The collection of measures forms a dimension, 
albeit a special one, called "Measures." 

### Hierarchy

Each cube dimension can contain a _hierarchy_ of levels to specify the categorical 
breakdown available to users. For example, the _geography_ dimension might include the 
following level hierarchy: Country, Province, District, Health Zone, Health Area. Each level in a 
dimension is of a finer grain than its parent. Similarly, the hierarchy of a time 
dimension might include levels for year, quarter, and month. 

A dimension can be created for use in an individual cube or in multiple cubes. A 
dimension created for an individual cube is called a private dimension, whereas a dimension that can
be used by multiple cubes is called a shared dimension. Shared 
dimensions enable the standardization of business metrics among cubes within a 
database.

### Member

One final important item of note is the concept of a member. A member is nothing 
more than an item in a dimension or measure. A calculated member is a dimension 
member whose value is calculated at run time using a specified expression. 

Calculated members can also be defined as measures. Only the definitions for 
calculated members are stored; values are calculated in memory when needed to 
answer a query. 

Calculated members enable you to add members and measures to a 
cube without increasing its size. Although calculated members must be based on a 
cube's existing data, you can create complex expressions by combining this data 
with arithmetic operators, numbers, and a variety of functions. 

Although the term "cube" suggests three dimensions, a cube can have up to 64 
dimensions, including the Measures dimension. In this article the expressions will at 
most retrieve two dimensions of data, rows and columns. This is analogous to the 
concept of a two-dimensional matrix. 
