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


(* Create a Map structure to store machine description *)
let map = T_M_Map.empty


(* Get first and third characters from each line -> the keys *)
(* Get second, fourth and fifth characters -> the data in the map *)
(* Put keys and data in the Map *)

(* Main interface. As an output it should give accept status, steps number
 * and end tape state *)
(* let run_machine input_syms m_desc = *)
