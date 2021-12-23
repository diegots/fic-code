(* ********************** Map definitions *********************************** *)
(* Key map definition and compare func for the the Map data structure *)
module Char_pairs =
    struct
        type t = char * char
        let compare (a,b) (c,d) =
            match Pervasives.compare a c with
            0 -> Pervasives.compare b d
            | x -> x
    end

module T_M_Map = Map.Make(Char_pairs)
(* ************************************************************************** *)


(* ********************** Fill a Map **************************************** *)
(* Extract the key part *)
let get_key s = s.[0], s.[2]
(* Extract arc part *)
let get_arc s = s.[1], s.[3], s.[4]

(* Fill Map with the machine description list *)
let rec m_desc_to_map l m = match l with
    | [] -> m
    | h::t -> m_desc_to_map t (T_M_Map.add (get_key h) (get_arc h) m)
(* ************************************************************************** *)


(* ********************** Print Map data ************************************ *)
let get_key key = (String.make 1 (fst key)) ^ (String.make 1 (snd key)) 
let get_arc arc = match arc with a,b,c ->
    (String.make 1 a) ^ (String.make 1 b) ^ (String.make 1 c) 

let print_machine key arc = (* key and arc are char actually *)
    print_string ((get_key key) ^ " " ^ (get_arc arc) ^ "\n")

(* debug : print map data *)
let print_m_desc m_desc =
    let map = T_M_Map.empty in
        T_M_Map.iter print_machine (m_desc_to_map m_desc map)

let rec print_list l = match l with
    | [] -> ()
    | [hd] -> print_string (String.make 1 hd)
    | hd :: tl -> print_string (String.make 1 hd); print_list tl
(* ************************************************************************** *)


(* ********************** Prepare the tape ********************************** *)
let rec prepare_tape l s =
    let sl = String.length s in
    if sl > 0 then
        prepare_tape (s.[0] :: l) (String.sub s 1 (sl-1))
    else List.rev l

let prepare_tape input_syms =
    if String.length input_syms = 0 then prepare_tape [] "B"
    else prepare_tape [] input_syms
(* ************************************************************************** *)


(* ********************** Turing Machine algorithm *************************** *)
let next_tran s map = T_M_Map.find s map

let move_right lt rt s = 
 (* print_endline "--move right--"; (* debug movements *)
    print_list lt; 
    print_string " | ";
    print_list rt;
    print_endline ("\n [Sym: " ^ (String.make 1 s) ^ "]"); *)
    match lt,rt with
    | e,[h]  -> (s::e), ['B']
    | e,h::t ->  (s::e), t
    | _,_    -> failwith "Invalid_right_move"

let move_left lt rt s = 
 (* print_endline "--move left--"; (* debug movements *)
    print_list lt; 
    print_string " | ";
    print_list rt;
    print_endline ("\n [Sym: " ^ (String.make 1 s) ^ "]"); *)
    match lt,rt with
    | [],[p] -> [], ('B'::s::[])   
    | [], p::q -> [], ('B'::s::q)
    | [h],_::q -> [], (h::s::q)
    | h::t,_::q -> t, (h::s::q)
    | _,_    -> failwith "Invalid_left_move"

(* As an output it should give accept status, steps number and end tape state *)
let rec run_machine lt rt st map steps = 
    (* try *) let st,sym,mov = next_tran (st, (List.hd rt)) map in
        match mov,st with 
            | 'R','H' -> true, (steps+1), (move_right lt rt sym) 
            | 'L','H' -> true, (steps+1), (move_left  lt rt sym)
            | 'R',_   -> let lt,rt = move_right lt rt sym in
                            run_machine lt rt st map (steps+1)
            | 'L',_   -> let lt,rt = move_left lt rt sym in
                            run_machine lt rt st map (steps+1)
            | _,_     -> false, steps, (lt, rt) (* Bad movement *) 
    (* with Not_found -> false, steps, (lt,rt) *)

(* Write tape's final version *)
let write_tape l = 
    List.fold_left (fun a b -> a ^ (String.make 1 b)) "" l

let write_tape lt rt = write_tape (List.rev_append ('\n'::lt) rt)
(* ************************************************************************** *)


(* ********************** Main interface ************************************ *)
(* Create initial structures and values. Then solve the problem. *)
let run_machine input_syms m_desc =
    let map        = m_desc_to_map m_desc (T_M_Map.empty) in (* machine's map *)
    let left_tape  = [] in (* left tape *)
    let right_tape = prepare_tape input_syms in
    let state      = (List.hd m_desc).[0] in (* first state *)
        (* print_endline ("First state: " ^ (String.make 1 state)); *) (* debug *)
    try let a,s,(lt,rt) = run_machine left_tape right_tape state map 0 in (* solve! *)
        a,s,(write_tape lt rt)
    with Not_found -> false, 0, ""

(* ************************************************************************** *)

(* ********************** Test write_tape *********************************** *)
let test_write_tape = 
    if not ((Pervasives.compare (write_tape ['b'; 'a'] ['c'; 'd']) "ab\ncd") = 0)  
    then failwith "write_tape";
    if not ((Pervasives.compare (write_tape [] ['c'; 'd']) "\ncd") = 0)  
    then failwith "write_tape"

let test_move_right =
    let lt,rt = [],['B'] in
        if not ((Pervasives.compare (move_right lt rt 'A') (['A'],['B'])) = 0) 
        then failwith "move_right";
    let lt,rt = [],['C';'D'] in
        if not ((Pervasives.compare (move_right lt rt 'J') (['J'],['D'])) = 0) 
        then failwith "move_right";
    let lt,rt = ['Q';'P'],['R'] in
        if not 
            ((Pervasives.compare (move_right lt rt 'Z') (['Z';'Q';'P'],['B'])) = 0) 
        then failwith "move_right";
    let lt,rt = ['B'],[] in 
        try let _ = move_right lt rt 'A' in ()
        with Failure "Invalid_right_move" -> () | _ -> failwith "move_right"

let test_move_left =
    let lt,rt = [],['B'] in
        if not ((Pervasives.compare (move_left lt rt 'A') ([],['B';'A'])) = 0) 
        then failwith "move_left";
    let lt,rt = ['J'],['P';'Q'] in
        if not ((Pervasives.compare (move_left lt rt 'A') ([],['J';'A';'Q'])) = 0) 
        then failwith "move_left";
    let lt,rt = ['R';'P';'J'],['T'] in
        if not 
            ((Pervasives.compare (move_left lt rt 'A') (['P';'J'],['R';'A'])) = 0) 
        then failwith "move_left"

let test_prepare_tape =
    let s = "abcdef" in
    let t = ['a'; 'b'; 'c'; 'd'; 'e'; 'f'] in
        if not ((Pervasives.compare (prepare_tape s) t) = 0)
        then failwith "prepare_tape";
    if not ((Pervasives.compare (prepare_tape "") ['B']) = 0)
    then failwith "prepare_tape"

let test_engine =
    try test_write_tape;
        test_move_right;
        test_move_left;
        test_prepare_tape
    with 
        | Failure "write_tape"
        | Failure "move_right"
        | Failure "move_left" -> exit(1)
(* ************************************************************************** *)
