(* Make ocaml-talf library functions available in ocaml top-level with: *)
(* #load "talf.cma";; *)
(* Or compile from inside ocaml-talf source directory: 
 * ocamlc *.cma ejercicios.ml -o ejercicios *)


(******************************************************************************)
(**************************** Automata definitions ****************************)
(******************************************************************************)
(* All states *)
let q0 = Auto.Estado "0";;
let q1 = Auto.Estado "1";;
let q2 = Auto.Estado "2";;
let q3 = Auto.Estado "3";;

(* Available simbols *)
let s1 = Auto.Terminal "a";;
let s2 = Auto.Terminal "b";;
let s3 = Auto.Terminal "c";;

(* Available transitions *)
let a1 = Auto.Arco_af (q0, q1, s1);;
let a2 = Auto.Arco_af (q1, q1, s2);;
let a3 = Auto.Arco_af (q1, q2, s1);;
let a4 = Auto.Arco_af (q2, q0, Auto.Terminal "");;
let a5 = Auto.Arco_af (q2, q3, Auto.Terminal "");;
let a6 = Auto.Arco_af (q2, q3, s1);;

(* States as a set *)
let q_states = Conj.Conjunto [q0; q1; q2; q3];;

(* Available simbols as a set *)
let sigma = Conj.Conjunto [s1; s2; s3];;

let initial_state = q0;;

(* Transitions as a set *)
let transitions = Conj.Conjunto [a1; a2; a3; a4; a5; a6];;

(* End states *)
let end_states = Conj.Conjunto [q0; q3];;

(* Automata definition au1 *)
let au1 = Auto.Af (q_states, sigma, initial_state, transitions, end_states);;

(* Automata definition au2 *)
let a1 = Auto.Arco_af (q0, q1, s1);;
let a2 = Auto.Arco_af (q1, q1, s2);;
let a3 = Auto.Arco_af (q1, q2, s1);;
let a4 = Auto.Arco_af (q2, q0, s3);;
let a5 = Auto.Arco_af (q2, q3, s1);; 
let a6 = Auto.Arco_af (q2, q3, s3);;
let transitions = Conj.Conjunto [a1; a2; a3; a4; a5; a6];;
let au2 = Auto.Af (q_states, sigma, initial_state, transitions, end_states);;

(* Automata definition au3 *)
let a2 = Auto.Arco_af (q0, q2, s1);;
let a3 = Auto.Arco_af (q1, q2, s1);;
let a4 = Auto.Arco_af (q2, q0, s3);;
let a5 = Auto.Arco_af (q2, q3, s1);;
let a6 = Auto.Arco_af (q2, q3, s2);;
let transitions = Conj.Conjunto [a2; a3; a4; a5; a6];;
let au3 = Auto.Af (q_states, sigma, initial_state, transitions, end_states);;

(* Automata definition au4 *)
let q0 = Auto.Estado "0";;
let q1 = Auto.Estado "1";;
let s1 = Auto.Terminal "a";;
let s2 = Auto.Terminal "b";;
let s3 = Auto.Terminal "c";;
let a1 = Auto.Arco_af (q0, q0, s1);;
let a2 = Auto.Arco_af (q0, q1, s1);;
let q_states = Conj.Conjunto [q0; q1];;
let sigma = Conj.Conjunto [s1; s2; s3];;
let initial_state = q0;;
let transitions = Conj.Conjunto [a1; a2];;
let end_states = Conj.Conjunto [q0];;
let au4 = Auto.Af (q_states, sigma, initial_state, transitions, end_states);;

(* Automata definition au5 *)
let au5 = "0 1 2 3; a b c; 0; 1 3; 0 1 a; 1 1 b; 1 2 a; 2 0 epsilon; 2 3 epsilon; 2 3 c;";;
let au5 = Ergo.af_of_string au5;;

let au6 = "0 1 2 3 4; a b c; 0; 3; 0 1 a; 1 2 b; 2 3 a; 0 2 a; 0 4 b;";;
let au6 = Ergo.af_of_string au6;; 

let au7 = "0 1 2 3 4; a b c; 0; 1 3; 0 1 a; 1 1 b; 1 2 a; 2 0 epsilon; 2 3 epsilon; 2 3 c; 0 4 b;";;
let au7 = Ergo.af_of_string au7;;

let au8 = "0 1 2 3 4 5; a b c; 0; 1 3; 0 1 a; 1 1 b; 1 2 a; 2 0 epsilon; 2 3 epsilon; 2 3 c; 5 5 b;";;
let au8 = Ergo.af_of_string au8;;

let au9 = "0 1 2 3 4; a b c; 0; 3; 0 1 a; 1 2 b; 2 3 a; 0 4 b;";;
let au9 = Ergo.af_of_string au9;; 


(******************************************************************************)
(***************************** Auxiliar functions *****************************)
(******************************************************************************)
(* get_arcs : draws conjunto of arcs from an automata *)
let get_arcs (autom:Auto.af) = match autom with
  | Auto.Af (_,_,_,t,_) -> t
;;
(* val get_arcs : Auto.af -> Auto.arco_af Conj.conjunto = <fun> *)

(* get_arc_symbol : draws the symbol in an arc definition *)
let get_arc_symbol = function
  | Auto.Arco_af (_,_,s) -> s
;;
(* val get_arc_symbol : Auto.arco_af -> Auto.simbolo = <fun> *)

(* get_arc_initial_node : draws the initial node in an arc definition *)
let get_arc_initial_node = function
  | Auto.Arco_af (inn,_,_) -> inn
;;
(* val get_arc_initial_node : Auto.arco_af -> Auto.estado = <fun> *)

(* get_arc_end_node : draws the end node in an arc definition *)
let get_arc_end_node = function
  | Auto.Arco_af (_,enn,_) -> enn
;;
(* val get_arc_end_node : Auto.arco_af -> Auto.estado = <fun> *)

(* get_states : draws conjunto of states from an automata *)
let get_states (autom:Auto.af) = match autom with
  | Auto.Af (states,_,_,_,_) -> states
;;
(* val get_states : Auto.af -> Auto.estado Conj.conjunto = <fun> *)

(* get_end_state : draws initial state from an automata *)
let get_end_state (autom:Auto.af) = match autom with
  | Auto.Af (_,_,_,_,end_state) -> end_state
;;
(* val get_end_state : Auto.af -> Auto.estado Conj.conjunto = <fun> *)

(* get_initial_state : draws conjunto of end states from an automata *)
let get_initial_state (autom:Auto.af) = match autom with
  | Auto.Af (_,_,initial_state,_,_) -> initial_state
;;
(* val get_initial_state : Auto.af -> Auto.estado = <fun> *)

(* get_alphabet_symbol : draws conjunto of symbols from an automata *)
let get_alphabet_symbol (autom:Auto.af) = match autom with
  | Auto.Af (_,symbol,_,_,_) -> symbol
;;
(* val get_alphabet_symbol : Auto.af -> Auto.simbolo Conj.conjunto = <fun> *)

(* get_list_states : draws conjunto of states, removes first one and returns it 
 * as a list *)
let get_list_states automata = 
  let s = get_initial_state automata in
    Conj.list_of_conjunto (Conj.suprimir s (get_states automata));;
(* val get_list_states : Auto.af -> Auto.estado list = <fun> *)

(* get_list_transitions : draws conjunto of transitions and returns it as a list *)
let get_list_transitions automata = Conj.list_of_conjunto (get_arcs automata);;
(* val get_list_transitions : Auto.af -> Auto.arco_af list = <fun> *)

(* get_relevant_transition : draws next valid transition fron a given state *)
let rec get_relevant_transition path state = function
  | [] -> failwith "get_relevant_transition"
  | h::t -> 
    if get_arc_initial_node h = state then 
      if List.mem (get_arc_end_node h) path then get_relevant_transition path state t
      else h,t
    else get_relevant_transition path state t
;;
(* val get_relevant_transition :
  Auto.estado -> Auto.arco_af list -> Auto.arco_af * Auto.arco_af list =
  <fun> *)

let get_next_state_afd state symbol automata =
  let arcs = get_arcs automata in
  let rec aux  = function
    | Conj.Conjunto [] -> failwith "get_relevant_transition_afd"
    | Conj.Conjunto (h::t) ->
      let i = get_arc_initial_node h in
      let s = get_arc_symbol h in
        if i = state && s = symbol then get_arc_end_node h
        else aux (Conj.Conjunto t)
  in aux arcs
;;


(******************************************************************************)
(*********************************** es_afne **********************************)
(******************************************************************************)
let es_afne automata = 
  let arcs = get_arcs automata in (* get conjunto of all arcs *)
  let rec es_afne = function (* for each arc... *)
    | Conj.Conjunto [] -> false
    | Conj.Conjunto (h::t) -> 
      let symbol = get_arc_symbol h in (* get its symbol an check if its epsilon *)
      if Conj.pertenece (Auto.Terminal "") (Conj.Conjunto [symbol]) then true
      else es_afne (Conj.Conjunto t)
  in es_afne arcs
;;

(* es_afne au1;; *)
(* es_afne au2;; *)
(* es_afne au3;; *)


(******************************************************************************)
(********************************** es_afn ************************************)
(******************************************************************************)
let rec check_afn_conditions t = function
  | Conj.Conjunto [] -> false
  | Conj.Conjunto (p::q) -> 
    get_arc_initial_node t = get_arc_initial_node p &&
    get_arc_end_node t = get_arc_end_node p &&
    get_arc_symbol t <> Auto.Terminal "" &&
    get_arc_symbol p <> Auto.Terminal "" &&
    get_arc_symbol t <> get_arc_symbol p ||
    check_afn_conditions t (Conj.Conjunto q)
;;

let es_afn automata =
  let arcs = get_arcs automata in (* get conjunto of all arcs *)
  let rec es_afn (* set_arcs *) = function (* match set_arcs with *)
    | Conj.Conjunto [] -> false
    | Conj.Conjunto (h::t) -> 
      let t = Conj.Conjunto t in check_afn_conditions h t || es_afn t
  in es_afn arcs
;;

(* es_afn au1;; *)
(* es_afn au2;; *)
(* es_afn au3;; *)


(******************************************************************************)
(********************************** es_afd ************************************)
(******************************************************************************)
let es_afd automata = es_afn automata && not (es_afne automata);;

(* es_afd au1;; *)
(* es_afd au2;; *)
(* es_afd au3;; *)


(******************************************************************************)
(********************************* es_conexo **********************************)
(******************************************************************************)
(* es_conexo must check three conditions:
 * 1. All automata states are reachable from the initial one. 
 * 2. There is one or more end states.
 * 3. All alphabet symbols are used in one transition at least *)

(* First condition *)
let all_reachable automata = 
  let arcs = get_list_transitions automata in (* all arcs *)
  let init_state = get_initial_state automata in
  let states = get_list_states automata in (* all states *)
  let rec aux current_state end_state left_arcs path =
    let _ = print_endline (match current_state with Auto.Estado a -> a) in
      if current_state = end_state then true
      else try
        let rt,tl = get_relevant_transition path current_state left_arcs 
        in aux (get_arc_end_node rt) end_state tl (current_state :: path)
      with Failure "get_relevant_transition" -> match path with
          | [] -> false
          | p::q -> aux p end_state left_arcs q (* backtrack *)
  in 
  let rec check_next = function 
    | [] -> true 
    | h::t -> 
    let _ = print_endline ("next: "^(match h with Auto.Estado a -> a)) 
    in aux init_state h arcs [initial_state] && check_next t
  in check_next states
;;

(* Second condition *)
let end_state_has_elements automata = 
  let end_node = get_end_state automata
  in not (Conj.es_vacio end_node)
;;

(* Third condition *)
(* build_conjunto_arc_sym : builds conjunto of arc symbols *)
let rec build_conjunto_arc_sym conj_syms = function
  | Conj.Conjunto [] -> conj_syms
  | Conj.Conjunto (h::t) -> 
    build_conjunto_arc_sym (Conj.agregar (get_arc_symbol h) conj_syms) (Conj.Conjunto t)
;;

let all_symbols_are_used automata = 
  let conj_syms = build_conjunto_arc_sym (Conj.conjunto_vacio) (get_arcs automata) in
  let sigma = get_alphabet_symbol automata in
    Conj.incluido sigma conj_syms
;;

let es_conexo automata = 
  all_reachable automata && 
  end_state_has_elements automata &&
  all_symbols_are_used automata
;;

(* let out_str = all_reachable au8 in print_endline (string_of_bool out_str);; *)
(* Graf.dibuja_af au8;; *)


(******************************************************************************)
(******************************** escaner_afn *********************************)
(******************************************************************************)
let escaner_afn cadena (Auto.Af (_, _, inicial, _, finales) as a) =

   let rec aux = function

        (Conj.Conjunto [], _) ->
           false

      | (actuales, []) ->
           not (Conj.es_vacio (Conj.interseccion actuales finales))

      | (actuales, simbolo :: t) ->
           aux (Auto.avanza simbolo actuales a, t)

   in
      aux (Conj.Conjunto [inicial], cadena)
   ;;

(* let cad1 = Ergo.cadena_of_string "a b";;
 * let cad2 = Ergo.cadena_of_string "a a";;
 * let cad3 = Ergo.cadena_of_string "a b a";;
 * let out_str = escaner_afn cad3 au6 in print_endline ("'" ^ 
 *   (Ergo.string_of_cadena cad3) ^ "' on automata is " ^ (string_of_bool out_str));;
 * Graf.dibuja_af au6;; *)


(******************************************************************************)
(******************************** escaner_afd *********************************)
(******************************************************************************)
let string_of_stado s = match s with Auto.Estado st -> st;;
let string_of_symbol s = match s with Auto.Terminal st -> st | Auto.No_terminal s -> s;;

let escaner_afd cadena (Auto.Af (_, _, inicial, _, finales) as a) =
  let rec aux state = function
    | [] -> not (Conj.es_vacio (Conj.interseccion (Conj.Conjunto [state]) finales))
    | h::t -> 
      try 
       let next_state = get_next_state_afd state h a in 
       let _ = print_endline ("from state: " ^ (string_of_stado state) ^ " with " ^ (string_of_symbol h) ^ " to " ^ (string_of_stado next_state)) in
         aux next_state t
      with Failure "get_relevant_transition_afd"  -> false
  in let _ = print_endline ("initial state: " ^ (string_of_stado inicial)) in aux inicial cadena
;;

(* let cad1 = Ergo.cadena_of_string "a b";;
 * let cad2 = Ergo.cadena_of_string "a a";;
 * let cad3 = Ergo.cadena_of_string "a b a";;
 * let out_str = escaner_afd cad2 au9 in print_endline ("'" ^ 
 *   (Ergo.string_of_cadena cad2) ^ "' on automata is " ^ (string_of_bool out_str));;
 * Graf.dibuja_af au9;; *)


