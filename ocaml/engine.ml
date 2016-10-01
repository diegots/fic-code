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

let move_right lt rt s = match lt,rt with
    | e,[h]   -> (s::e), ['B']
    | e,h::t ->  (s::e), t
    | _,_   -> failwith "Invalid_tape_state"

let move_left lt rt s = match lt,rt with
    | [],e   -> [],(s::e)
    | h::t,e -> t ,(s::e)

(* As an output it should give accept status, steps number and end tape state *)
let rec run_machine lt rt st map steps = 
    try let st,sym,mov = next_tran (st, (List.hd rt)) map in
        match mov,st with 
            | 'R','H' -> true, (steps+1), (move_right lt rt sym)
            | 'L','H' -> true, (steps+1), (move_left  lt rt sym)
            | 'R',_ ->  let lt,rt = move_right lt rt sym in
                            run_machine lt rt st map (steps+1)
            | 'L',_ ->  let lt,rt = move_left lt rt sym in
                            run_machine lt rt st map (steps+1)
            | _,_ -> failwith "Invalid_movement"
    with Not_found -> false, steps, (lt, rt)
(* ************************************************************************** *)

(* ************************************************************************** *)
let writeTape l = List.fold_left (fun a b -> a ^ (String.make 1 b)) "" l
let writeTape lt rt = writeTape (List.rev_append lt rt)
(* ************************************************************************** *)


(* ********************** Main interface ************************************ *)
(* Create initial structures and values. Then solve the problem. *)
let run_machine input_syms m_desc =
    let map        = m_desc_to_map m_desc (T_M_Map.empty) in (* machine's map *)
    let left_tape  = [] in (* left tape *)
    let right_tape = prepare_tape input_syms in (* right tape *)
    let state      = (List.hd m_desc).[0] in (* current state *)

    let a,s,(lt,rt) = run_machine left_tape right_tape state map 1 in (* solve! *)
        a, s, (writeTape lt rt)
