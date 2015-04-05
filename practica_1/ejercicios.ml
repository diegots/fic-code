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
let a1 = Auto.Arco_af (q0, q1, s1);;
let a2 = Auto.Arco_af (q0, q2, s1);;
let a3 = Auto.Arco_af (q1, q2, s1);;
let a4 = Auto.Arco_af (q2, q0, s3);;
let a5 = Auto.Arco_af (q2, q3, s1);;
let a6 = Auto.Arco_af (q2, q3, s2);;
let transitions = Conj.Conjunto [a2; a3; a4; a5; a6];;
let au3 = Auto.Af (q_states, sigma, initial_state, transitions, end_states);;


(******************************************************************************)
(***************************** Auxiliar functions *****************************)
(******************************************************************************)
(* get_arcs : draws conjunto of arcs from an automata *)
let get_arcs (autom:Auto.af) = match autom with
  | Auto.Af (_,_,_,t,_) -> t
;;

(* get_arc_symbol : draws the symbol in an arc definition *)
let get_arc_symbol = function
  | Auto.Arco_af (_,_,s) -> s
;;

(* get_arc_initial_node : draws the initial node in an arc definition *)
let get_arc_initial_node = function
  | Auto.Arco_af (inn,_,_) -> inn
;;

(* get_arc_end_node : draws the end node in an arc definition *)
let get_arc_end_node = function
  | Auto.Arco_af (_,enn,_) -> enn
;;

(* get_states : draws conjunto of states from an automata *)
let get_states (autom:Auto.af) = match autom with
  | Auto.Af (states,_,_,_,_) -> states
;;

(* get_end_state : draws initial state from an automata *)
let get_end_state (autom:Auto.af) = match autom with
  | Auto.Af (_,_,_,_,end_state) -> end_state
;;

(* get_initial_state : draws conjunto of end states from an automata *)
let get_initial_state (autom:Auto.af) = match autom with
  | Auto.Af (_,_,initial_state,_,_) -> initial_state
;;

(* get_alphabet_symbol : draws conjunto of symbols from an automata *)
let get_alphabet_symbol (autom:Auto.af) = match autom with
  | Auto.Af (_,symbol,_,_,_) -> symbol
;;

(* get_list_states : draws conjunto of states and returns it as a list *)
let get_list_states automata = Conj.list_of_conjunto (get_states automata);;

(* get_list_transitions : draws conjunto of transitions and returns it as a list *)
let get_list_transitions automata = Conj.list_of_conjunto (get_arcs automata);;

(* get_relevant_transition : draws next available transition fron a state *)
let rec get_relevant_transition state = function
  | [] -> failwith "get_relevant_transition"
  | h::t -> 
    if get_arc_initial_node h = state then h,t 
    else get_relevant_transition state t
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
(*
let f automata = 
  let states = get_list_states automata in (* all states *)
  let transitions = get_list_transitions automata in (* all transitions *)
  let rec g (ns,nt) (ks,kt) = match ns with
    | [] -> true (* all states are reachable, end *) 
    | h::t -> match nt with (* there are states to check *)
      | [] -> if  (* TODO if there is previous, travel to previous else false *)
      | p::q -> (* TODO transitions not empty *)
  in g (states, transitions) ([],[])
;;

let f automata = 
  let states = get_list_states automata in (* all states *)
  let transitions = get_list_transitions automata in (* all transitions *)
 
  let rec g (ns,nt) pe = match ns with
    | [] -> true (* all states are reachable, end *) 
    | h::t -> match nt with (* there are states to check *)
      | [] -> 
        if List.length >= 1 then (* if there are previous states, do backtrack *)
          let (ks,kt) = List.hd pe in g () (List.tl pe)
        else false (* No more transitions to check, end *)
      | p::q ->  (* TODO transitions not empty *)
        
  in g (states, transitions) [([],[])]
;;
*)

let f automata = 
  let states = get_list_states automata in (* all states *)
  let arcs = get_list_transitions automata in (* all transitions *)
  let init_state = get_initial_state automata in
  let rec g end_state current_state arcs left_arcs =
    if end_state = current_state then true
    else 
      let rt,tl = (get_relevant_transition current_state arcs) 
      in g end_state (get_arc_end_node rt) arcs tl
  in g end_state (List.head states) arcs [] (* TODO define end state *)
;;


(* Previous code that it's not going to be used again, i think.
 * TODO delete
let build_pairs automata = 
  let ini_state = get_initial_state automata in
  let states = Conj.suprimir ini_state (get_states automata) in
  let rec build_pairs = function
    | Conj.Conjunto [] -> []
    | Conj.Conjunto (h::t) -> (ini_state, h) :: build_pairs (Conj.Conjunto t)
  in build_pairs states
;;

let is_compatible_trans state trans = state = (get_arc_initial_node trans);;
(* is_compatible_trans q0 a1;; *) (* must be true with au1 *)
(* is_compatible_trans q0 a2;; *) (* must be false with au1 *)

let check_pair pair transition = match transition with
  | [] -> (* if not reached -> false || get not not taken transition *)
  | h::t -> 

let all_states_reachable automata =
  let pairs = build_pairs automata in (* all posible pairs *)
  let trans = get_arcs automata in (* all arcs in an automata *)
  let rec all_states_reachable = function (* check pair one by one *)
    | [] -> true
    | h::t -> check_pair trans h && all_states_reachable t
  in all_states_reachable pairs
;;
*)

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
  (* TODO first condition *)
  end_state_has_elements automata &&
  all_symbols_are_used automata
;;
