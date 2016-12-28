/* Examples for testing */

true; /* true and false are recognized values */
if false then true else false; 

x/; /* Slash operator bounds a name */
x;

x = true;
x;
if x then false else x; 

lambda x. x; /* bounded lambda-term */
(lambda x. x) (lambda x. x x); 

{x=lambda x.x, y=(lambda x.x)(lambda x.x)}; 
{x=lambda x.x, y=(lambda x.x)(lambda x.x)}.x; 

"hello";

timesfloat (timesfloat 2.0 3.0) (timesfloat 4.0 5.0);

0; 
succ (pred 0);
iszero (pred (succ (succ 0))); 

let x=true in x;
