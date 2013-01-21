//
//  ProfileViewController.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@interface ProfileViewController : GAITrackedViewController
@property(weak, nonatomic) IBOutlet UITableView *tableView;
-(IBAction)logout:(id)sender;
-(IBAction)about:(id)sender;
-(IBAction)createPlaylist:(id)sender;
@end
