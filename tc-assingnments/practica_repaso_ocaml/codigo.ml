(***************)
(* Ejercicio 1 *)
(***************)
let rec mapdoble f1 f2 = function
| [h1] -> f1 h1 :: []
| h1::h2::t -> (f1 h1) :: (f2 h2) :: mapdoble f1 f2 t
| [] -> []
;;
(* val mapdoble : ('a -> 'b) -> ('a -> 'b) -> 'a list -> 'b list = <fun> *)

mapdoble (function x -> x) (function x -> -x) [1;1;1;1;1];;
(* - : int list = [1; -1; 1; -1; 1] *)

(* mapdoble (function x -> x*2) (function x -> "x") [1;2;3;4;5];; *)
(* Error de tipos, la segunda función trata strings y la primera ints, nótese
 * que las funcíones van ambas de 'a -> 'b, esto es, deben tratar los mismos
 * tipos. *)

let y = function x -> 5 in mapdoble y;;
(* - : ('_a -> int) -> '_a list -> int list = <fun> *)
(* El tipo devuelto es una función parcial. *)

(***************)
(* Ejercicio 2 *)
(***************)
let rec primero_que_cumple f = function
| h::t -> if f h then h else primero_que_cumple f t
| _ -> failwith "No_such_element"
;;
(* val primero_que_cumple : ('a -> bool) -> 'a list -> 'a = <fun> *)

primero_que_cumple (function x -> x>0) [-1; -5; 0; 10];;
(* - : int = 10 *)
primero_que_cumple (function x -> x>0) [-1; -5; 0; -1];;
(* Exception: Failure "No_such_element". *)

let existe f l =
  try let _ = primero_que_cumple f l in true
  with Failure "No_such_element" -> false;;
(* val existe : ('a -> bool) -> 'a list -> bool = <fun> *)

existe (function x -> x>0) [-1; -5; 0; 10];;
(* - : bool = true *)

existe (function x -> x>0) [-1; -5; 0; -3];;
(* - : bool = false *)

(* Por la definición de conjunto se asume que no hay elementos repetidos y que
 * tampoco hay orden entre los elementos *)
let rec asociado set key = match set with
  | [] -> failwith "No_such_element"
  | (a,b)::t ->
    try
      primero_que_cumple (function e -> e = key) [a]; b
    with Failure "No_such_element" -> asociado t key
;;
(* val asociado : ('a * 'b) list -> 'a -> 'b = <fun>*)

asociado [('a',1); ('b',2); ('c',3); ('d',4)] 'd';;
(* - : int = 4 *)

asociado [('a',1); ('b',2); ('c',3); ('d',4)] 'e';;
(* Exception: Failure "No_such_element". *)

(***************)
(* Ejercicio 3 *)
(***************)

type 'a arbol_binario =
    Vacio
  | Nodo of 'a * 'a arbol_binario * 'a arbol_binario
;;

let t = Nodo(3,
  Nodo(2, Vacio, Vacio),
  Nodo(5, Nodo(4, Vacio, Vacio), Nodo(1, Vacio, Vacio)))
;;

let t0 = Vacio;;

(* Rama derecha, nodo, rama izquierda *)
let rec in_orden = function
  | Vacio -> []
  | Nodo (tag, left, right) -> (in_orden left) @ [tag] @ (in_orden right)
;;
(* val in_orden : 'a arbol_binario -> 'a list = <fun> *)

in_orden t;;
(* - : int list = [2; 3; 4; 5; 1] *)

let rec pre_orden = function
  | Vacio -> []
  | Nodo (tag, left, right) -> [tag] @ (pre_orden left) @ (pre_orden right)
;;
(* val pre_orden : 'a arbol_binario -> 'a list = <fun> *)

pre_orden t;;
(* - : int list = [3; 2; 5; 4; 1] *)

let rec post_orden = function
  | Vacio -> []
  | Nodo (tag, left, right) -> (post_orden left) @ (post_orden right) @ [tag]
;;
(* val post_orden : 'a arbol_binario -> 'a list = <fun> *)

post_orden t;;
(* - : int list = [2; 4; 1; 5; 3] *)

let anchura tree =
  let rec aux tree_ queue = match tree_,queue with
    Vacio,[] -> []
  | Vacio,h::t-> aux h t
  | Nodo (tag, left, right),[] -> tag :: aux left [right]
  | Nodo (tag, left, right),h::t -> tag :: aux h (left :: right :: t)
in aux tree []
;;
(* val anchura : 'a arbol_binario -> 'a list = <fun> *)

anchura t;;
(* - : int list = [3; 2; 5; 4; 1] *)

(***************)
(* Ejercicio 4 *)
(***************)

type 'a conjunto = Conjunto of 'a list;;
(* type 'a conjunto = Conjunto of 'a list *)

let conjunto_vacio = Conjunto [];;
(* val conjunto_vacio : 'a conjunto = Conjunto [] *)

let set0 = Conjunto [];;
let set1 = Conjunto ['a'; 'b'];;
let set2 = Conjunto ["uno"; "dos"; "tres"];;
let set3 = Conjunto [1; 2];;
let set6 = Conjunto [1; 2; 3; 4; 5; 6];;
let set7 = Conjunto [-1; 0; 1; 2];;

(* Función pertenece *)
let rec pertenece element = function
  | Conjunto e ->
    match e with
      | [] -> false
      | h::t -> if h=element then true else pertenece element (Conjunto t)
;;
(* val pertenece : 'a -> 'a conjunto -> bool = <fun> *)

pertenece 1 set0;;
(* pertenece 1 set1;; *) (* Type error *)
pertenece "uno" set2;;
pertenece "cinco" set2;;

(* Función agregar *)
let agregar element set =
  if pertenece element set then set
  else match set with
    | Conjunto e ->
      match e with
        | [] -> Conjunto [element]
        | h::t -> Conjunto (element::h::t)
;;
(* val agregar : 'a -> 'a conjunto -> 'a conjunto = <fun> *)

(* agregar 1 set1;; *)
agregar 1 set3;;
agregar 1 set0;;
agregar 3 set3;;

(* Función conjunto_of_list *)
let conjunto_of_list list =
  let rec aux set = function
    | [] -> set
    | h::t -> let set_ = agregar h set in aux set_ t
  in aux conjunto_vacio list
;;
(* val conjunto_of_list : 'a list -> 'a conjunto = <fun> *)

let set4 = conjunto_of_list [1;2;3;4;5;6];;
let set5 = conjunto_of_list [1;1;1;1];;
(* set5 = conjunto_of_list ['a'];; *) (* Error de tipos*)

(* Función suprimir *)
let suprimir element set =
  let rec aux nuevo_set old_set = match nuevo_set,old_set with
    | Conjunto [], Conjunto [] -> conjunto_vacio
    | Conjunto (h::t), Conjunto [] -> nuevo_set
    | _, Conjunto (h::t) ->
      if h=element then aux nuevo_set (Conjunto t)
      else aux (agregar h nuevo_set) (Conjunto t)
  in aux conjunto_vacio set
;;
(* val suprimir : 'a -> 'a conjunto -> 'a conjunto = <fun> *)

suprimir 1 set5;;
(* - : int conjunto = Conjunto [] *)

(* Función cardinal *)
let rec cardinal = function
  | Conjunto [] -> 0
  | Conjunto (h::t) -> 1 + cardinal (Conjunto t)
;;
(* val cardinal : 'a conjunto -> int = <fun> *)

cardinal set0;;
(* - : int = 0 *)
cardinal set1;;
(* - : int = 2 *)

(* Función union *)
let union set1 set2 =
  let rec aux nuevo_set set1 set2 = match set1,set2 with
    | Conjunto [], Conjunto []             -> nuevo_set
    | Conjunto (h1::t1), Conjunto (h2::t2) ->
      let nuevo_set = agregar h1 nuevo_set in
      let nuevo_set = agregar h2 nuevo_set in
      aux nuevo_set (Conjunto t1) (Conjunto t2)
    | Conjunto [], Conjunto (h::t)
    | Conjunto (h::t), Conjunto []         ->
      aux (agregar h nuevo_set) (Conjunto t) conjunto_vacio
  in aux conjunto_vacio set1 set2
;;
(* val union : 'a conjunto -> 'a conjunto -> 'a conjunto = <fun> *)

union set3 set6;;
(* - : int conjunto = Conjunto [6; 5; 4; 3; 2; 1] *)

(* Función interseccion *)
let interseccion set1 set2 =
  let rec aux nuevo_set set1_ set2_ = match set1_,set2_ with
    | Conjunto [], Conjunto []             -> nuevo_set
    | Conjunto (h1::t1), Conjunto (h2::t2) ->
      if pertenece h1 set2 then let nuevo_set = agregar h1 nuevo_set in
        if pertenece h2 set1 then let nuevo_set = agregar h2 nuevo_set in
          aux nuevo_set (Conjunto t1) (Conjunto t2)
        else aux nuevo_set (Conjunto t1) (Conjunto t2)
      else aux nuevo_set (Conjunto t1) (Conjunto t2)
    | Conjunto [], Conjunto (h::t)
    | Conjunto (h::t), Conjunto []         ->
      if pertenece h set1 && pertenece h set2 then
        let nuevo_set = agregar h nuevo_set in
        aux nuevo_set (Conjunto t) conjunto_vacio
      else nuevo_set
  in aux conjunto_vacio set1 set2
;;
(* val interseccion : 'a conjunto -> 'a conjunto -> 'a conjunto = <fun> *)

interseccion set3 set6;;
(* - : int conjunto = Conjunto [2; 1] *)
interseccion set6 set6;;
(* - : int conjunto = Conjunto [6; 5; 4; 3; 2; 1] *)

(* Función diferencia *)
(* Elementos del primer conjunto que no están en el segundo. *)
let diferencia set1 set2 =
  let inters = interseccion set1 set2 in
  let rec aux nuevo_set = function
    | Conjunto [] -> nuevo_set
    | Conjunto (h::t) -> aux (suprimir h nuevo_set) (Conjunto t)
  in aux set1 inters
;;
(* val diferencia : 'a conjunto -> 'a conjunto -> 'a conjunto = <fun> *)

diferencia set6 set7;;
(* )- : int conjunto = Conjunto [3; 4; 5; 6] *)

(* Función incluido *)
let rec incluido set1 set2 = match set1 with
  | Conjunto [] -> true
  | Conjunto (h::t) -> pertenece h set2 && incluido (Conjunto t) set2
;;
(* val incluido : 'a conjunto -> 'a conjunto -> bool = <fun> *)

incluido set0 set1;;
(* - : bool = true *)
incluido set1 set0;;
(* - : bool = false *)

(* Función igual *)
let igual set1 set2 =
  if cardinal set1 = cardinal set2 then
    let rec aux = function
      | Conjunto [] -> true
      | Conjunto (h::t) -> pertenece h set2 && aux (Conjunto t)
    in aux set1
  else false
;;
(* val igual : 'a conjunto -> 'a conjunto -> bool = <fun> *)

igual set3 set0;;
(* - : bool = false *)
igual set3 set3;;
(* - : bool = true *)

(* Función producto_cartesiano *)
let rec producto_cartesiano elem set new_set =
match set with
  | Conjunto [] -> new_set
  | Conjunto (h::t) ->
    producto_cartesiano elem (Conjunto t) (agregar (elem,h) new_set)
;;
let producto_cartesiano set1 set2 =
  let rec aux new_set = function
    | Conjunto [] -> new_set
    | Conjunto (h::t) ->
      let new_set_ = producto_cartesiano h set2 new_set
      in aux new_set_ (Conjunto t)
  in aux (Conjunto []) set1
;;
(* val producto_cartesiano : 'a conjunto -> 'b conjunto -> ('a * 'b) conjunto =
  <fun> *)

producto_cartesiano set1 set2;;
(*  - : (char * string) conjunto = Conjunto
   [('b', "tres"); ('b', "dos"); ('b', "uno"); ('a', "tres"); ('a', "dos");
    ('a', "uno")] *)

(* Función list_of_conjunto *)
let rec list_of_conjunto = function
  | Conjunto [] -> []
  | Conjunto (h::t) -> h::(list_of_conjunto (Conjunto t))
;;
(* val list_of_conjunto : 'a conjunto -> 'a list = <fun> *)

set1;;
(* - : char conjunto = Conjunto ['a'; 'b'] *)
list_of_conjunto set1;;
(* - : char list = ['a'; 'b'] *)

set6;;
(* - : int conjunto = Conjunto [1; 2; 3; 4; 5; 6] *)
list_of_conjunto set6;;
(* - : int list = [1; 2; 3; 4; 5; 6] *)
