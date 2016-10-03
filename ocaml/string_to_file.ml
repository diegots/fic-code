open Printf;;

let string_to_file s file = 
	let oc = open_out file in
	fprintf oc "%s" s;
	close_out oc;;