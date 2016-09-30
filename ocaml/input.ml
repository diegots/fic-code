
(* Get cli parameters: machine description and out file path *)
let get_params input_args =
    let input_length = Array.length input_args in
        if input_length = 2 then
            let mDesc = input_args.(1) in mDesc,""
        else if input_length = 3 then
            let mDesc = input_args.(1) in
            let outPath = input_args.(2) in mDesc,outPath
        else "","";;

(* Request tape syms to the user *)
let get_input () = 
    print_string "Input: ";
    let input = read_line () in input;;

(* Key comparision in the Map structure *)
module CharPairs =
    struct
        type t = char * char
        let compare (a,b) (c,d) =
            match Pervasives.compare a c with
                0 -> Pervasives.compare b d
              | c -> c
    end;;

module TuringMachineMap = Map.Make(CharPairs);;

(* Create a Map structure to store machine description *)
let map = TuringMachineMap.empty;;

(* Read machine description line by line *)
(*let in_channel = open_in path in
try
    while true do
        let line = input_line in_channel in
            (* Get first and third characters from each line -> these are the keys *)
            (* Get second, fourth and fifth characters -> the data in the map *)
            (* Put keys and data in the Map *)
    done
with End_of_file ->
    close_in in_channel;;
    *)

